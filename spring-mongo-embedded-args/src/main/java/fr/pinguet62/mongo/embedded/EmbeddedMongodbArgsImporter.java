package fr.pinguet62.mongo.embedded;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class EmbeddedMongodbArgsImporter implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        Map<String, String> args = extractArgsFromAnnotations(importingClassMetadata);
        registerBeanPostProcessor(args, registry);
    }

    private Map<String, String> extractArgsFromAnnotations(AnnotationMetadata importingClassMetadata) {
        Map<String, String> args = new HashMap<>();

        Map<String, Object> repeatableAnnotation = importingClassMetadata.getAnnotationAttributes(EmbeddedMongodbArgs.class.getName());
        if (repeatableAnnotation != null)
            args.putAll(
                    Arrays.stream((AnnotationAttributes[]) repeatableAnnotation.get("value"))
                            .map(this::annotationToArg)
                            .collect(Collectors.toMap(
                                    Entry::getKey,
                                    Entry::getValue)));

        Map<String, Object> simpleAnnotation = importingClassMetadata.getAnnotationAttributes(EmbeddedMongodbArg.class.getName());
        if (simpleAnnotation != null)
            args.putAll(Map.ofEntries(annotationToArg(simpleAnnotation)));

        return args;
    }

    /**
     * @param simpleAnnotation {@link EmbeddedMongodbArg}
     */
    private Entry<String, String> annotationToArg(Map<String, Object> simpleAnnotation) {
        return Map.entry(
                (String) simpleAnnotation.get("key"),
                (String) simpleAnnotation.get("value"));
    }

    private void registerBeanPostProcessor(Map<String, String> args, BeanDefinitionRegistry registry) {
        AppendArgsToMongodConfigBeanPostProcessor beanPostProcessor = new AppendArgsToMongodConfigBeanPostProcessor(args);
        BeanDefinition beanDefinition = new RootBeanDefinition(AppendArgsToMongodConfigBeanPostProcessor.class, () -> beanPostProcessor);
        registry.registerBeanDefinition(AppendArgsToMongodConfigBeanPostProcessor.class.getName(), beanDefinition);
    }
}
