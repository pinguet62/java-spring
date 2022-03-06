package fr.pinguet62.test.springtransactional.config.transaction;

import org.springframework.transaction.ReactiveTransaction;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @see org.springframework.data.transaction.MultiTransactionStatus
 */
public class ReactiveMultiTransactionStatus implements ReactiveTransaction {

    private final ReactiveTransactionManager mainTransactionManager;
    private final Map<ReactiveTransactionManager, ReactiveTransaction> transactionStatuses = Collections.synchronizedMap(new HashMap<>());

    private boolean newSynchonization;

    public ReactiveMultiTransactionStatus(ReactiveTransactionManager mainTransactionManager) {
        Assert.notNull(mainTransactionManager, "TransactionManager must not be null!");
        this.mainTransactionManager = mainTransactionManager;
    }

    public Map<ReactiveTransactionManager, ReactiveTransaction> getTransactionStatuses() {
        return transactionStatuses;
    }

    public void setNewSynchonization() {
        newSynchonization = true;
    }

    public boolean isNewSynchonization() {
        return newSynchonization;
    }

    @Override
    public boolean isNewTransaction() {
        return newSynchonization;
    }

    public Mono<Void> registerTransactionManager(TransactionDefinition definition, ReactiveTransactionManager transactionManager) {
        return transactionManager.getReactiveTransaction(definition)
                .doOnNext(reactiveTransaction -> getTransactionStatuses().put(transactionManager, reactiveTransaction))
                .then();
    }

    public Mono<Void> commit(ReactiveTransactionManager transactionManager) {
        ReactiveTransaction transactionStatus = getTransactionStatus(transactionManager);
        return transactionManager.commit(transactionStatus);
    }

    public Mono<Void> rollback(ReactiveTransactionManager transactionManager) {
        return transactionManager.rollback(getTransactionStatus(transactionManager));
    }

    @Override
    public void setRollbackOnly() {
        for (ReactiveTransaction ts : transactionStatuses.values()) {
            ts.setRollbackOnly();
        }
    }

    @Override
    public boolean isRollbackOnly() {
        return getMainTransactionStatus().isRollbackOnly();
    }

    @Override
    public boolean isCompleted() {
        return getMainTransactionStatus().isCompleted();
    }

    private ReactiveTransaction getMainTransactionStatus() {
        return transactionStatuses.get(mainTransactionManager);
    }

    private ReactiveTransaction getTransactionStatus(ReactiveTransactionManager transactionManager) {
        return getTransactionStatuses().get(transactionManager);
    }
}
