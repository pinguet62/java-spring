package fr.pinguet62.springkafkalistener.customizer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;

import java.util.List;

@RequiredArgsConstructor
public class KafkaListenerContainerFactoryCustomizerPostProcessor implements BeanPostProcessor {

    private final List<KafkaListenerEndpointCustomizer> kafkaListenerEndpointCustomizers;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof KafkaListenerContainerFactory<?> kafkaListenerContainerFactory) {
            return new KafkaListenerContainerFactoryAdapter(kafkaListenerContainerFactory) {
                @Override
                public MessageListenerContainer createListenerContainer(KafkaListenerEndpoint endpoint) {
                    if (endpoint instanceof MethodKafkaListenerEndpoint methodKafkaListenerEndpoint)
                        kafkaListenerEndpointCustomizers.forEach(customizer -> customizer.customize(methodKafkaListenerEndpoint));
                    return super.createListenerContainer(endpoint);
                }
            };
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
