package fr.pinguet62.springreactivekafka;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Map;

@AutoConfiguration(after = KafkaAutoConfiguration.class)
@ConditionalOnClass({EnableKafka.class, KafkaReceiver.class})
@Import(ReactiveKafkaAnnotationDrivenConfiguration.class)
public class ReactiveKafkaAutoConfiguration {

    @ConditionalOnMissingBean
    @Bean
    ReceiverOptions<?, ?> receiverOptions(
            KafkaProperties properties,
            ObjectProvider<ReceiverOptionsCustomizer<Object, Object>> customizers) {
        Map<String, Object> props = properties.buildConsumerProperties(null);

        ReceiverOptions<Object, Object> receiverOptions = ReceiverOptions.create(props);

        for (ReceiverOptionsCustomizer<Object, Object> customizer : customizers.orderedStream().toList())
            receiverOptions = customizer.customize(receiverOptions);

        return receiverOptions;
    }
}
