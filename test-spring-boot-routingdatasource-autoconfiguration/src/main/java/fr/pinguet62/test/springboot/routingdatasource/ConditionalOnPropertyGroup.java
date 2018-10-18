package fr.pinguet62.test.springboot.routingdatasource;

import fr.pinguet62.test.springboot.routingdatasource.ConditionalOnPropertyGroup.OnPropertyGroupCondition;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.env.RandomValuePropertySource;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySource.StubPropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jndi.JndiPropertySource;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.boot.autoconfigure.condition.ConditionMessage.forCondition;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.match;
import static org.springframework.boot.autoconfigure.condition.ConditionOutcome.noMatch;

/**
 * {@link @Conditional} who apply {@link String#startsWith(String)} predicate to {@link Environment} values.
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Conditional(OnPropertyGroupCondition.class)
public @interface ConditionalOnPropertyGroup {

    /**
     * The beginning of property name.<br>
     * Should not end with {@code "."} character.
     */
    String value();

    /**
     * @see String#startsWith(String)
     */
    class OnPropertyGroupCondition extends SpringBootCondition {

        private static final Logger LOGGER = getLogger(OnPropertyGroupCondition.class);

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Map<String, Object> conditionalOnPropertyGroup = metadata.getAnnotationAttributes(ConditionalOnPropertyGroup.class.getName());
            String propertyNamePattern = (String) conditionalOnPropertyGroup.get("value");

            Map<String, Object> properties = getAllProperties((AbstractEnvironment) context.getEnvironment());

            for (String propertyName : properties.keySet())
                if (propertyName.startsWith(propertyNamePattern + "."))
                    return match(forCondition(ConditionalOnPropertyGroup.class).found("property group \"" + propertyNamePattern + "\" found").items(propertyName));

            return noMatch(forCondition(ConditionalOnPropertyGroup.class).didNotFind("property group \"" + propertyNamePattern + "\" not found").atAll());
        }

        private Map<String, Object> getAllProperties(AbstractEnvironment abstractEnvironment) {
            Map<String, Object> properties = new HashMap<>();
            for (PropertySource<?> propertySource : abstractEnvironment.getPropertySources())
                properties.putAll(getAllProperties(propertySource));
            return properties;
        }

        private Map<String, Object> getAllProperties(PropertySource<?> propertySource) {
            Map<String, Object> properties = new HashMap<>();

            // Filter supported types
            if (asList(RandomValuePropertySource.class, StubPropertySource.class, JndiPropertySource.class).contains(propertySource.getClass()))
                return properties; // not useful
            if (!(propertySource instanceof EnumerablePropertySource)) {
                LOGGER.warn("Skip unsupported {} type: {}", PropertySource.class.getSimpleName(), propertySource.getClass().getName());
                return properties; // not supported
            }

            EnumerablePropertySource<?> enumerablePropertySource = (EnumerablePropertySource<?>) propertySource;
            for (String propertyName : enumerablePropertySource.getPropertyNames())
                properties.put(propertyName, enumerablePropertySource.getProperty(propertyName));
            return properties;
        }

    }

}
