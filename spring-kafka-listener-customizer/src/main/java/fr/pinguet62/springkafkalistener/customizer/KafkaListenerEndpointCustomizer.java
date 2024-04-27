package fr.pinguet62.springkafkalistener.customizer;

import org.springframework.kafka.config.MethodKafkaListenerEndpoint;

public interface KafkaListenerEndpointCustomizer<K, V> {

    void customize(MethodKafkaListenerEndpoint<K, V> endpoint);
}
