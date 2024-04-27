package fr.pinguet62.springkafkalistener.override;

import fr.pinguet62.springkafkalistener.customizer.KafkaListenerEndpointCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.lang.reflect.Method;

/**
 * Override specific {@link KafkaListener} (using {@link Method#toString()} as key) from properties.
 */
@AutoConfiguration(after = KafkaListenerEndpointCustomizerAutoConfiguration.class)
@ConditionalOnClass(KafkaTemplate.class)
public class OverrideKafkaListenerAutoConfiguration {

    @Bean
    public OverrideConfigKafkaListenerEndpointCustomizer overrideConfigKafkaListenerEndpointCustomizer(Environment environment) {
        return new OverrideConfigKafkaListenerEndpointCustomizer(environment);
    }
}
