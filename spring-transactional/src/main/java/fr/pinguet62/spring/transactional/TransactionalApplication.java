package fr.pinguet62.spring.transactional;

import fr.pinguet62.spring.transactional.config.kafka.ReactiveKafkaTransactionManager;
import fr.pinguet62.spring.transactional.config.transaction.ReactiveChainedTransactionManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.ReactiveMongoTransactionManager;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class TransactionalApplication {

    @Bean
    ReactiveTransactionManager chainedTransactionManager(
            ReactiveKafkaProducerTemplate<?, ?> reactiveKafkaProducerTemplate,
            ReactiveMongoDatabaseFactory dbFactory) {
        return new ReactiveChainedTransactionManager(
                new ReactiveKafkaTransactionManager(reactiveKafkaProducerTemplate),
                new ReactiveMongoTransactionManager(dbFactory));
    }
}
