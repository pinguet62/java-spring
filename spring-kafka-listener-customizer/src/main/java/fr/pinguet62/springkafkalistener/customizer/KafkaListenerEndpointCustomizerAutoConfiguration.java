package fr.pinguet62.springkafkalistener.customizer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

/**
 * Define {@link KafkaListenerEndpointCustomizer} to modify {@link KafkaListener}.
 */
@AutoConfiguration(after = KafkaAutoConfiguration.class)
@ConditionalOnClass(KafkaTemplate.class)
public class KafkaListenerEndpointCustomizerAutoConfiguration {

    @Bean
    @ConditionalOnBean(KafkaListenerContainerFactory.class)
    public KafkaListenerContainerFactoryCustomizerPostProcessor kafkaListenerContainerFactoryCustomizerPostProcessor(List<KafkaListenerEndpointCustomizer> kafkaListenerEndpointCustomizers) {
        return new KafkaListenerContainerFactoryCustomizerPostProcessor(kafkaListenerEndpointCustomizers);
    }
}
