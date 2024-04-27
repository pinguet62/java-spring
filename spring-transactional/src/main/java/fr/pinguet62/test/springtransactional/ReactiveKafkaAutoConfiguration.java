package fr.pinguet62.test.springtransactional;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class ReactiveKafkaAutoConfiguration {

    @Bean
    ReactiveKafkaProducerTemplate<?, ?> reactiveKafkaProducerTemplate(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties(null);
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }
}
