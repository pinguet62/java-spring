package fr.pinguet62.springkafkalistener.customizer;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.TopicPartitionOffset;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class KafkaListenerContainerFactoryAdapter<C extends MessageListenerContainer> implements KafkaListenerContainerFactory<C> {

    private final KafkaListenerContainerFactory<C> parent;

    @Override
    public C createListenerContainer(KafkaListenerEndpoint endpoint) {
        return parent.createListenerContainer(endpoint);
    }

    @Override
    public C createContainer(TopicPartitionOffset... topicPartitions) {
        return parent.createContainer(topicPartitions);
    }

    @Override
    public C createContainer(String... topics) {
        return parent.createContainer(topics);
    }

    @Override
    public C createContainer(Pattern topicPattern) {
        return parent.createContainer(topicPattern);
    }
}
