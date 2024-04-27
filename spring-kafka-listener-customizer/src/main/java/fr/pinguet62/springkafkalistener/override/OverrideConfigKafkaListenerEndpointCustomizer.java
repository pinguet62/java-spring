package fr.pinguet62.springkafkalistener.override;

import fr.pinguet62.springkafkalistener.customizer.KafkaListenerEndpointCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;

import java.lang.reflect.Method;
import java.util.Map;

@RequiredArgsConstructor
public class OverrideConfigKafkaListenerEndpointCustomizer implements KafkaListenerEndpointCustomizer {

    private final Environment environment;

    @Override
    public void customize(MethodKafkaListenerEndpoint endpoint) {
        BindResult<KafkaListenerMethodsProperties> bind = Binder.get(environment).bind("kafka", KafkaListenerMethodsProperties.class);
        if (!bind.isBound())
            return;
        Map<String, KafkaListenerProperties> propertiesByMethod = bind.get().getListeners();

        Method method = endpoint.getMethod();
        KafkaListenerProperties kafkaListenerProperties = propertiesByMethod.get(method.toString());
        if (kafkaListenerProperties == null)
            return;

        if (kafkaListenerProperties.getGroupId() != null)
            endpoint.setGroupId(kafkaListenerProperties.getGroupId());
        if (kafkaListenerProperties.getTopics() != null)
            endpoint.setTopics(kafkaListenerProperties.getTopics().toArray(String[]::new));
        if (kafkaListenerProperties.getProperties() != null)
            endpoint.setConsumerProperties(kafkaListenerProperties.getProperties());
    }
}
