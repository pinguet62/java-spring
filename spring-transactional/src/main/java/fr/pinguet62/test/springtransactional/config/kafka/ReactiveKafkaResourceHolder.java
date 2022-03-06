package fr.pinguet62.test.springtransactional.config.kafka;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.support.ResourceHolderSupport;
import reactor.kafka.sender.TransactionManager;

/**
 * Inspired by {@link org.springframework.data.mongodb.ReactiveMongoResourceHolder}.
 */
@RequiredArgsConstructor
class ReactiveKafkaResourceHolder extends ResourceHolderSupport {

    @NonNull
    @Getter
    private TransactionManager transactionManager;
}
