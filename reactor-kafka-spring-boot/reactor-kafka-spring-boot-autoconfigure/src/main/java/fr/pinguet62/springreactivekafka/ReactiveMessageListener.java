package fr.pinguet62.springreactivekafka;

import org.reactivestreams.Publisher;
import org.springframework.kafka.listener.adapter.RecordMessagingMessageListenerAdapter;
import reactor.kafka.receiver.ReceiverRecord;

import java.lang.reflect.Method;

/**
 * Process the {@link ReceiverRecord} and call the {@link Method} annotated with {@link ReactiveKafkaListener}.
 * <p>
 * Hack around existing {@link RecordMessagingMessageListenerAdapter}:
 * save {@link #handleResult(Object, Object, Object)} be be returned in {@link #onMessage(ReceiverRecord)}.
 *
 * @see RecordMessagingMessageListenerAdapter
 * @see org.springframework.kafka.listener.adapter.HandlerAdapter
 * @see org.springframework.messaging.handler.invocation.InvocableHandlerMethod
 */
public class ReactiveMessageListener<K, V> extends RecordMessagingMessageListenerAdapter<K, V> {

    public ReactiveMessageListener(Object bean, Method method) {
        super(bean, method);
    }

    private Object currentResult = null;

    // TODO better solution than `synchronized`
    public synchronized Publisher<Object> onMessage(ReceiverRecord<K, V> record) {
        currentResult = null; // clean
        super.onMessage(record, null, null);
        return (Publisher<Object>) currentResult;
    }

    /**
     * Default behavior: reply message (not supported).
     */
    @Override
    protected void handleResult(Object resultArg, Object request, Object source) {
        currentResult = resultArg;
    }
}
