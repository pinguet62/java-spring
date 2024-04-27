package fr.pinguet62.springreactivekafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.SmartLifecycle;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;
import java.util.Properties;

import static java.lang.Integer.MAX_VALUE;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

/**
 * Unitary container managing the {@link KafkaReceiver}.
 *
 * @see org.springframework.kafka.listener.ConcurrentMessageListenerContainer
 * @see org.springframework.kafka.listener.KafkaMessageListenerContainer
 */
@CommonsLog
@RequiredArgsConstructor
public class ReactiveMessageListenerContainer<K, V> implements SmartLifecycle, DisposableBean {

    private final ReceiverOptions<K, V> receiverOptions;
    private final Properties annotationProperties;
    private final String groupId;
    private final String topic;

    private final ReactiveMessageListener<K, V> messageListener;
    private final ReactiveCommonErrorHandler reactiveCommonErrorHandler;

    private volatile Disposable disposable;

    @Override
    public boolean isRunning() {
        return disposable != null;
    }

    @Override
    public void start() {
        if (!isRunning()) {
            ReceiverOptions<K, V> customizedReceiverOptions = receiverOptions;

            for (var property : annotationProperties.entrySet())
                customizedReceiverOptions = customizedReceiverOptions.consumerProperty((String) property.getKey(), property.getValue());
            customizedReceiverOptions = customizedReceiverOptions.consumerProperty(GROUP_ID_CONFIG, groupId);

            disposable = new ReactiveKafkaConsumerTemplate<>(
                    KafkaReceiver.create(
                            customizedReceiverOptions
                                    .subscription(List.of(topic))))
                    .receive()
                    .groupBy(receiverRecord -> receiverRecord.receiverOffset().topicPartition())
                    .flatMap(partition -> partition.flatMapSequential(receiverRecord ->
                                            Mono.defer(() -> Flux.from(messageListener.onMessage(receiverRecord)).then())
                                                    .retryWhen(reactiveCommonErrorHandler.getRetry(receiverRecord))
                                                    .thenReturn(receiverRecord),
                                    1),
                            MAX_VALUE/*if number of partitions > max concurrency (256 by default)*/)
                    .doOnNext(receiverRecord -> receiverRecord.receiverOffset().acknowledge())
                    .subscribe(
                            null,
                            err -> log.error("Error in Kafka receiver: stopped!", err));
        }
    }

    @Override
    public void stop() {
        if (isRunning()) {
            disposable.dispose();
            disposable = null;
        }
    }

    @Override
    public void destroy() {
        stop();
    }
}
