package fr.pinguet62.springreactivekafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.listener.SeekUtils;
import reactor.kafka.receiver.ReceiverOffset;

/**
 * Default behaviors:
 * <ul>
 *     <li>process each {@link ConsumerRecord#partition()} in parallel</li>
 *     <li>auto-{@link ReceiverOffset#acknowledge()} message when process success</li>
 *     <li>retry instantly 10 times, when process error</li>
 * </ul>
 */
@Import({
        ReactiveKafkaListenerAnnotationBeanPostProcessor.class,
        ReactiveKafkaListenerEndpointRegistry.class,
})
public class ReactiveKafkaAnnotationDrivenConfiguration {

    @ConditionalOnMissingBean
    @Bean
    ReactiveCommonErrorHandler reactiveCommonErrorHandler() {
        return new ReactiveCommonErrorHandler(
                (consumerRecord, exception) -> true, // TODO customizable
                SeekUtils.DEFAULT_BACK_OFF); // TODO customizable
    }
}
