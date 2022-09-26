package fr.pinguet62.mongo.embedded;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

import static lombok.AccessLevel.MODULE;

@RequiredArgsConstructor
public class AppendArgsToMongodConfigBeanPostProcessor implements BeanPostProcessor {

    @NonNull
    @Getter(MODULE)
    private final Map<String, String> args;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof MongodConfig mongodConfig) {
            return MongodConfig.builder()
                    .from(mongodConfig)
                    .putAllArgs(args)
                    .build();
        }
        return bean;
    }
}
