package fr.pinguet62.springreactivekafka;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.SmartLifecycle;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Use {@link ReactiveKafkaListenerEndpoint} and build {@link ReactiveMessageListenerContainer}.
 * Manage lifecycle of {@link ReactiveMessageListenerContainer}: start & stop.
 *
 * @see org.springframework.kafka.config.KafkaListenerEndpointRegistry
 */
@RequiredArgsConstructor
public class ReactiveKafkaListenerEndpointRegistry implements SmartLifecycle, DisposableBean {

    private final ReceiverOptions<?, ?> receiverOptions;
    private final ReactiveCommonErrorHandler reactiveCommonErrorHandler;

    private final List<ReactiveMessageListenerContainer<?, ?>> listenerContainers = new ArrayList<>();

    @Getter
    private volatile boolean running;

    public void registerListenerContainer(ReactiveKafkaListenerEndpoint<?, ?> endpoint) {
        ReactiveMessageListenerContainer<?, ?> container = createListenerContainer(endpoint);
        listenerContainers.add(container);
    }

    private <K, V> ReactiveMessageListenerContainer<?, ?> createListenerContainer(ReactiveKafkaListenerEndpoint<K, V> endpoint) {
        return new ReactiveMessageListenerContainer<>(
                (ReceiverOptions<K, V>) receiverOptions,
                endpoint.getProperties(),
                endpoint.getGroupId(),
                endpoint.getTopic(),
                endpoint.createMessageListener(),
                reactiveCommonErrorHandler);
    }

    @Override
    public void start() {
        listenerContainers.forEach(ReactiveMessageListenerContainer::start);
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        listenerContainers.forEach(ReactiveMessageListenerContainer::stop);
    }

    @Override
    public void destroy() {
        listenerContainers.forEach(ReactiveMessageListenerContainer::destroy);
    }
}
