package fr.pinguet62.springreactivekafka;

import lombok.NonNull;
import lombok.Value;
import org.springframework.kafka.listener.adapter.HandlerAdapter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.invocation.InvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Properties;

@Value
public class ReactiveKafkaListenerEndpoint<K, V> {

    @NonNull
    Object bean;
    @NonNull
    Method method;

    @NonNull
    Properties properties;
    @NonNull
    String groupId;
    @NonNull
    String topic;

    public ReactiveMessageListener<K, V> createMessageListener() {
        var messageListener = new ReactiveMessageListener<K, V>(bean, method);
        messageListener.setHandlerMethod(configureListenerAdapter());
        return messageListener;
    }

    private HandlerAdapter configureListenerAdapter() {
        DefaultMessageHandlerMethodFactory messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory(); // simple (limited) initialization
        InvocableHandlerMethod invocableHandlerMethod = messageHandlerMethodFactory.createInvocableHandlerMethod(getBean(), getMethod());
        messageHandlerMethodFactory.afterPropertiesSet();
        return new HandlerAdapter(invocableHandlerMethod);
    }
}
