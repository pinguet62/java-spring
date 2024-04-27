package fr.pinguet62.spring.transactional.config.transaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.transaction.*;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

/**
 * @see org.springframework.data.transaction.ChainedTransactionManager
 */
public class ReactiveChainedTransactionManager implements ReactiveTransactionManager {

    private final static Log logger = LogFactory.getLog(ReactiveChainedTransactionManager.class);

    private final List<ReactiveTransactionManager> transactionManagers;

    public ReactiveChainedTransactionManager(ReactiveTransactionManager... transactionManagers) {
        Assert.isTrue(transactionManagers.length > 0, "At least one ReactiveTransactionManager must be given!");
        this.transactionManagers = asList(transactionManagers);
    }

    public Mono<ReactiveTransaction> getReactiveTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
        ReactiveMultiTransactionStatus mts = new ReactiveMultiTransactionStatus(transactionManagers.get(0));

        if (definition == null) {
            return Mono.just(mts);
        }

        return TransactionSynchronizationManager.forCurrentTransaction()
                .flatMap(synchronizationManager -> {
                    if (!synchronizationManager.isSynchronizationActive()) {
                        synchronizationManager.initSynchronization();
                        mts.setNewSynchonization();
                    }

                    return Flux.fromIterable(transactionManagers)
                            .flatMap(transactionManager -> mts.registerTransactionManager(definition, transactionManager),
                                    1)
                            .onErrorResume(Exception.class, registerException -> {
                                Map<ReactiveTransactionManager, ReactiveTransaction> transactionStatuses = mts.getTransactionStatuses();

                                return Flux.fromIterable(transactionManagers)
                                        .flatMap(transactionManager -> {
                                            if (transactionStatuses.get(transactionManager) != null) {
                                                return transactionManager.rollback(transactionStatuses.get(transactionManager))
                                                        .onErrorResume(Exception.class, rollbackException -> {
                                                            logger.warn("Rollback exception (" + transactionManager + ") " + rollbackException.getMessage(), rollbackException);
                                                            return Mono.empty();
                                                        });
                                            }
                                            return Mono.empty();
                                        }, 1)
                                        .then(Mono.fromRunnable(() -> {
                                            if (mts.isNewSynchonization()) {
                                                synchronizationManager.clearSynchronization();
                                            }

                                            throw new CannotCreateTransactionException(registerException.getMessage(), registerException);
                                        }));
                            })
                            .then(Mono.just(mts));
                });
    }

    public Mono<Void> commit(ReactiveTransaction status) throws TransactionException {
        ReactiveMultiTransactionStatus multiTransactionStatus = (ReactiveMultiTransactionStatus) status;

        Supplier<Flux<ReactiveTransactionManager>> reverseTransactionManagers = buildReverse(transactionManagers);
        return reverseTransactionManagers.get().flatMap(transactionManagerToCommit ->
                                multiTransactionStatus.commit(transactionManagerToCommit)
                                        .onErrorResume(Exception.class, commitException ->
                                                reverseTransactionManagers.get().flatMap(transactionManagerToRollback ->
                                                                        multiTransactionStatus.rollback(transactionManagerToRollback)
                                                                                .onErrorResume(Exception.class, rollbackException -> {
                                                                                    logger.warn("Rollback exception (after commit) (" + transactionManagerToRollback + ") " + rollbackException.getMessage(), rollbackException);
                                                                                    return Mono.empty();
                                                                                }),
                                                                1)
                                                        .then(clearSynchronizationIfIsNewSynchonization(multiTransactionStatus))
                                                        .then(Mono.fromRunnable(() -> {
                                                            boolean firstTransactionManagerFailed = transactionManagerToCommit == transactionManagers.get(transactionManagers.size() - 1);
                                                            int transactionState = firstTransactionManagerFailed ? HeuristicCompletionException.STATE_ROLLED_BACK
                                                                    : HeuristicCompletionException.STATE_MIXED;
                                                            throw new HeuristicCompletionException(transactionState, commitException);
                                                        }))),
                        1)
                .then(clearSynchronizationIfIsNewSynchonization(multiTransactionStatus));
    }

    public Mono<Void> rollback(ReactiveTransaction status) throws TransactionException {
        ReactiveMultiTransactionStatus multiTransactionStatus = (ReactiveMultiTransactionStatus) status;

        Supplier<Flux<ReactiveTransactionManager>> reverseTransactionManagers = buildReverse(transactionManagers);
        return reverseTransactionManagers.get().flatMap(transactionManagerToRollback ->
                                multiTransactionStatus.rollback(transactionManagerToRollback)
                                        .onErrorResume(Exception.class, rollbackException ->
                                                reverseTransactionManagers.get().flatMap(nextTransactionManagerToRollbackSafety ->
                                                                multiTransactionStatus.rollback(nextTransactionManagerToRollbackSafety)
                                                                        .onErrorResume(Exception.class, nextSafetyRollbackException -> {
                                                                            logger.warn("Rollback exception (" + transactionManagerToRollback + ") " + nextSafetyRollbackException.getMessage(), nextSafetyRollbackException);
                                                                            return Mono.empty();
                                                                        }))
                                                        .then(clearSynchronizationIfIsNewSynchonization(multiTransactionStatus))
                                                        .then(Mono.error(new UnexpectedRollbackException("Rollback exception, originated at (" + transactionManagerToRollback + ") " + rollbackException.getMessage(), rollbackException)))),
                        1)
                .then(clearSynchronizationIfIsNewSynchonization(multiTransactionStatus));
    }

    private Mono<Void> clearSynchronizationIfIsNewSynchonization(ReactiveMultiTransactionStatus multiTransactionStatus) {
        return TransactionSynchronizationManager.forCurrentTransaction()
                .doOnNext(synchronizationManager -> {
                    if (multiTransactionStatus.isNewSynchonization()) {
                        synchronizationManager.clearSynchronization();
                    }
                })
                .then();
    }

    private <T> Supplier<Flux<T>> buildReverse(Collection<T> original) {
        Deque<T> deque = new LinkedList<>(original);
        return () -> Flux.generate(sink -> {
            var element = deque.pollLast();
            if (element == null) {
                sink.complete();
            } else {
                sink.next(element);
            }
        });
    }
}
