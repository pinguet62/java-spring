package fr.pinguet62.springreactivekafka;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

/**
 * Find and parse {@link ReactiveKafkaListener}, build {@link ReactiveKafkaListenerEndpoint}, and register in {@link ReactiveKafkaListenerEndpointRegistry}.
 *
 * @see org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor
 */
@RequiredArgsConstructor
public class ReactiveKafkaListenerAnnotationBeanPostProcessor<K, V> implements BeanPostProcessor {

    private final BeanFactory beanFactory;
    private final ReactiveKafkaListenerEndpointRegistry endpointRegistry;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        Map<Method, ReactiveKafkaListener> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                (MetadataLookup<ReactiveKafkaListener>) method -> AnnotationUtils.findAnnotation(method, ReactiveKafkaListener.class));

        annotatedMethods.forEach((method, listener) -> processKafkaListener(bean, method, listener));

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    private synchronized void processKafkaListener(Object bean, Method method, ReactiveKafkaListener kafkaListener) {
        ReactiveKafkaListenerEndpoint<?, ?> endpoint = new ReactiveKafkaListenerEndpoint<K, V>(
                bean,
                method,
                resolveKafkaProperties(kafkaListener.properties()),
                resolveExpression(kafkaListener.groupId()),
                resolveExpression(kafkaListener.topic()));
        endpointRegistry.registerListenerContainer(endpoint);
    }

    private Properties resolveKafkaProperties(String[] propertyStrings) {
        Properties properties = new Properties();
        for (String property : propertyStrings) {
            property = resolveExpression(property);
            try (StringReader reader = new StringReader(property)) {
                properties.load(reader);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return properties;
    }

    private String resolveExpression(String value) {
        BeanExpressionResolver resolver = beanFactory instanceof ConfigurableListableBeanFactory clbf ? clbf.getBeanExpressionResolver() : new StandardBeanExpressionResolver();
        BeanExpressionContext expressionContext = new BeanExpressionContext((ConfigurableBeanFactory) beanFactory, null);
        value = beanFactory instanceof ConfigurableBeanFactory cbf ? cbf.resolveEmbeddedValue(value) : value;
        return (String) resolver.evaluate(value, expressionContext);
    }
}
