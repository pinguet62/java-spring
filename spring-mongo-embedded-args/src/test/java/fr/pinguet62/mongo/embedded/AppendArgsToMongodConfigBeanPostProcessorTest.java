package fr.pinguet62.mongo.embedded;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

import static de.flapdoodle.embed.mongo.distribution.Version.Main.PRODUCTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;

class AppendArgsToMongodConfigBeanPostProcessorTest {

    @Test
    void shouldAppendArgsToExistingBean() {
        MongodConfig existingMongodConfig = MongodConfig.builder()
                .version(PRODUCTION)
                .putArgs("initial", "preserved")
                .putArgs("another", "also-preserved")
                .build();

        BeanPostProcessor beanPostProcessor = new AppendArgsToMongodConfigBeanPostProcessor(Map.ofEntries(
                Map.entry("first", "foo"),
                Map.entry("second", "bar")));
        Object updatedMongodConfig = beanPostProcessor.postProcessAfterInitialization(existingMongodConfig, "mongodConfig");

        assertThat(((MongodConfig) updatedMongodConfig).args(), allOf(
                hasEntry("initial", "preserved"),
                hasEntry("another", "also-preserved"),
                hasEntry("first", "foo"),
                hasEntry("second", "bar")));
    }
}
