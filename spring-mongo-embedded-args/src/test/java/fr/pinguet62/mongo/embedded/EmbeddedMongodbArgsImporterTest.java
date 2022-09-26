package fr.pinguet62.mongo.embedded;

import org.junit.jupiter.api.Test;
import org.springframework.boot.context.annotation.UserConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasKey;

class EmbeddedMongodbArgsImporterTest {

    @EmbeddedMongodbArg(key = "single")
    static class SingleSimpleAnnotation {
    }

    @Test
    void whenSingleSimpleAnnotation() {
        new ApplicationContextRunner()
                .withConfiguration(UserConfigurations.of(SingleSimpleAnnotation.class))
                .run(context -> assertThat(context.getBean(AppendArgsToMongodConfigBeanPostProcessor.class).getArgs(),
                        allOf(
                                hasKey("single"))));
    }

    @EmbeddedMongodbArg(key = "first")
    @EmbeddedMongodbArg(key = "second")
    static class MultipleSimpleAnnotation {
    }

    @Test
    void whenMultipleSimpleAnnotations() {
        new ApplicationContextRunner()
                .withConfiguration(UserConfigurations.of(MultipleSimpleAnnotation.class))
                .run(context -> assertThat(context.getBean(AppendArgsToMongodConfigBeanPostProcessor.class).getArgs(),
                        allOf(
                                hasKey("first"),
                                hasKey("second"))));
    }

    @EmbeddedMongodbArgs({
            @EmbeddedMongodbArg(key = "first"),
            @EmbeddedMongodbArg(key = "second"),
    })
    static class SingleRepeatableAnnotation {
    }

    @Test
    void whenSingleRepeatableAnnotations() {
        new ApplicationContextRunner()
                .withConfiguration(UserConfigurations.of(SingleRepeatableAnnotation.class))
                .run(context -> assertThat(context.getBean(AppendArgsToMongodConfigBeanPostProcessor.class).getArgs(),
                        allOf(
                                hasKey("first"),
                                hasKey("second"))));
    }

    @EmbeddedMongodbArgs({
            @EmbeddedMongodbArg(key = "first"),
            @EmbeddedMongodbArg(key = "second"),
    })
    @EmbeddedMongodbArg(key = "third")
    static class RepeatableAndSimpleAnnotations {
    }

    @Test
    void whenRepeatableAndSimpleAnnotations() {
        new ApplicationContextRunner()
                .withConfiguration(UserConfigurations.of(RepeatableAndSimpleAnnotations.class))
                .run(context -> assertThat(context.getBean(AppendArgsToMongodConfigBeanPostProcessor.class).getArgs(),
                        allOf(
                                hasKey("first"),
                                hasKey("second"),
                                hasKey("third"))));
    }
}
