package fr.pinguet62.flapdoodle.mongo.args;

import de.flapdoodle.embed.mongo.commands.MongodArguments;
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
        if (bean instanceof MongodArguments mongodConfig) {
            return MongodArguments.builder()
                    .from(mongodConfig)
                    .putAllArgs(args)
                    .build();
        }
        return bean;
    }
}
