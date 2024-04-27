package fr.pinguet62.flapdoodle.mongo.args;

import de.flapdoodle.embed.mongo.commands.MongodArguments;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;

class AppendArgsToMongodConfigBeanPostProcessorTest {

    @Test
    void shouldAppendArgsToExistingBean() {
        MongodArguments existingMongodConfig = MongodArguments.builder()
                .putArgs("initial", "preserved")
                .putArgs("another", "also-preserved")
                .build();

        BeanPostProcessor beanPostProcessor = new AppendArgsToMongodConfigBeanPostProcessor(Map.ofEntries(
                Map.entry("first", "foo"),
                Map.entry("second", "bar")));
        Object updatedMongodConfig = beanPostProcessor.postProcessAfterInitialization(existingMongodConfig, "mongodConfig");

        assertThat(((MongodArguments) updatedMongodConfig).args(), allOf(
                hasEntry("initial", "preserved"),
                hasEntry("another", "also-preserved"),
                hasEntry("first", "foo"),
                hasEntry("second", "bar")));
    }
}
