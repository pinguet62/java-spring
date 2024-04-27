package fr.pinguet62.spring.transactional.config.kafka;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.reactive.AbstractReactiveTransactionManager;
import org.springframework.transaction.reactive.GenericReactiveTransaction;
import org.springframework.transaction.reactive.TransactionSynchronizationManager;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.TransactionManager;

/***
 * Inspired by {@link org.springframework.data.mongodb.ReactiveMongoTransactionManager}.
 */
@RequiredArgsConstructor
public class ReactiveKafkaTransactionManager extends AbstractReactiveTransactionManager {

    @NonNull
    private final ReactiveKafkaProducerTemplate<?, ?> reactiveKafkaProducerTemplate;

    @Override
    protected Object doGetTransaction(TransactionSynchronizationManager synchronizationManager) throws TransactionException {
        ReactiveKafkaResourceHolder resourceHolder = (ReactiveKafkaResourceHolder) synchronizationManager.getResource(reactiveKafkaProducerTemplate);
        return new ReactiveKafkaTransactionObject(resourceHolder);
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
        return extractKafkaTransaction(transaction).hasResourceHolder();
    }

    @Override
    protected Mono<Void> doBegin(TransactionSynchronizationManager synchronizationManager, Object transaction, TransactionDefinition definition) throws TransactionException {
        return Mono.defer(() -> {
            ReactiveKafkaTransactionObject kafkaTransactionObject = extractKafkaTransaction(transaction);
            return newResourceHolder(definition)
                    .doOnNext(resourceHolder -> {
                        kafkaTransactionObject.setResourceHolder(resourceHolder);
                        if (logger.isDebugEnabled())
                            logger.debug(
                                    String.format("About to start transaction for transaction %s.", resourceHolder.getTransactionManager()));
                    })
                    .doOnNext(resourceHolder -> {
                        if (logger.isDebugEnabled())
                            logger.debug(String.format("Started transaction for TransactionManager %s.", resourceHolder.getTransactionManager()));
                    })
                    .flatMap(resourceHolder -> kafkaTransactionObject.startTransaction()
                            .thenReturn(resourceHolder))
                    .onErrorMap(ex -> new TransactionSystemException(String.format("Could not start Kafka transaction for TransactionManager %s.", kafkaTransactionObject.getTransactionManager()), ex))
                    .doOnSuccess(resourceHolder -> synchronizationManager.bindResource(reactiveKafkaProducerTemplate, resourceHolder))
                    .then();
        });
    }

    @Override
    protected Mono<Object> doSuspend(TransactionSynchronizationManager synchronizationManager, Object transaction) throws TransactionException {
        return Mono.fromSupplier(() -> {
            ReactiveKafkaTransactionObject kafkaTransactionObject = extractKafkaTransaction(transaction);
            kafkaTransactionObject.setResourceHolder(null);
            return synchronizationManager.unbindResource(reactiveKafkaProducerTemplate);
        });
    }

    @Override
    protected Mono<Void> doResume(TransactionSynchronizationManager synchronizationManager, @Nullable Object transaction, Object suspendedResources) {
        return Mono.fromRunnable(() -> synchronizationManager.bindResource(reactiveKafkaProducerTemplate, suspendedResources));
    }

    @Override
    protected final Mono<Void> doCommit(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) throws TransactionException {
        return Mono.defer(() -> {
            ReactiveKafkaTransactionObject kafkaTransactionObject = extractKafkaTransaction(status);
            if (logger.isDebugEnabled())
                logger.debug(String.format("About to commit transaction for TransactionManager %s.", kafkaTransactionObject.getTransactionManager()));
            return doCommit(synchronizationManager, kafkaTransactionObject)
                    .onErrorMap(ex -> new TransactionSystemException(String.format("Could not commit Kafka transaction for TransactionManager %s.", kafkaTransactionObject.getTransactionManager()), ex));
        });
    }

    protected Mono<Void> doCommit(TransactionSynchronizationManager synchronizationManager, ReactiveKafkaTransactionObject transactionObject) {
        return transactionObject.commitTransaction();
    }

    @Override
    protected Mono<Void> doRollback(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) {
        return Mono.defer(() -> {
            ReactiveKafkaTransactionObject kafkaTransactionObject = extractKafkaTransaction(status);
            if (logger.isDebugEnabled())
                logger.debug(String.format("About to abort transaction for TransactionManager %s.", kafkaTransactionObject.getTransactionManager()));
            return kafkaTransactionObject.abortTransaction();
        });
    }

    @Override
    protected Mono<Void> doSetRollbackOnly(TransactionSynchronizationManager synchronizationManager, GenericReactiveTransaction status) throws TransactionException {
        return Mono.fromRunnable(() -> {
            ReactiveKafkaTransactionObject transactionObject = extractKafkaTransaction(status);
            transactionObject.getRequiredResourceHolder().setRollbackOnly();
        });
    }

    @Override
    protected Mono<Void> doCleanupAfterCompletion(TransactionSynchronizationManager synchronizationManager, Object transaction) {
        Assert.isInstanceOf(ReactiveKafkaTransactionObject.class, transaction, () -> String.format("Expected to find a %s but it turned out to be %s.", ReactiveKafkaTransactionObject.class, transaction.getClass()));
        return Mono.fromRunnable(() -> {
            ReactiveKafkaTransactionObject kafkaTransactionObject = (ReactiveKafkaTransactionObject) transaction;

            // Remove the connection holder from the thread.
            synchronizationManager.unbindResource(reactiveKafkaProducerTemplate);
            kafkaTransactionObject.getRequiredResourceHolder().clear();

            if (logger.isDebugEnabled())
                logger.debug(String.format("About to release TransactionManager %s after transaction.", kafkaTransactionObject.getTransactionManager()));
        });
    }

    private Mono<ReactiveKafkaResourceHolder> newResourceHolder(TransactionDefinition definition) {
        return Mono.just(new ReactiveKafkaResourceHolder(this.reactiveKafkaProducerTemplate.transactionManager()));
    }

    private static ReactiveKafkaTransactionObject extractKafkaTransaction(Object transaction) {
        Assert.isInstanceOf(ReactiveKafkaTransactionObject.class, transaction, () -> String.format("Expected to find a %s but it turned out to be %s.", ReactiveKafkaTransactionObject.class, transaction.getClass()));
        return (ReactiveKafkaTransactionObject) transaction;
    }

    private static ReactiveKafkaTransactionObject extractKafkaTransaction(GenericReactiveTransaction status) {
        Assert.isInstanceOf(ReactiveKafkaTransactionObject.class, status.getTransaction(), () -> String.format("Expected to find a %s but it turned out to be %s.", ReactiveKafkaTransactionObject.class, status.getTransaction().getClass()));
        return (ReactiveKafkaTransactionObject) status.getTransaction();
    }

    @AllArgsConstructor
    protected static class ReactiveKafkaTransactionObject implements SmartTransactionObject {

        @Nullable
        private ReactiveKafkaResourceHolder resourceHolder;

        void setResourceHolder(@Nullable ReactiveKafkaResourceHolder resourceHolder) {
            this.resourceHolder = resourceHolder;
        }

        final boolean hasResourceHolder() {
            return resourceHolder != null;
        }

        Mono<Void> startTransaction() {
            return Mono.defer(() -> {
                TransactionManager transactionManager = getRequiredTransactionManager();
                return transactionManager.begin();
            });
        }

        public Mono<Void> commitTransaction() {
            return getRequiredTransactionManager().commit();
        }

        public Mono<Void> abortTransaction() {
            return getRequiredTransactionManager().abort();
        }

        @Nullable
        public TransactionManager getTransactionManager() {
            return resourceHolder != null ? resourceHolder.getTransactionManager() : null;
        }

        private ReactiveKafkaResourceHolder getRequiredResourceHolder() {
            Assert.state(resourceHolder != null, "ReactiveKafkaResourceHolder is required but not present. o_O");
            return resourceHolder;
        }

        private TransactionManager getRequiredTransactionManager() {
            TransactionManager transactionManager = getTransactionManager();
            Assert.state(transactionManager != null, "A TransactionManager is required but it turned out to be null.");
            return transactionManager;
        }

        @Override
        public boolean isRollbackOnly() {
            return resourceHolder != null && resourceHolder.isRollbackOnly();
        }

        @Override
        public void flush() {
            throw new UnsupportedOperationException("flush() not supported");
        }
    }
}
