package fr.pinguet62.jdbc.routingdatasource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class OnPropertyGroupConditionTest {

    @Configuration
    @ConditionalOnPropertyGroup("root.sub")
    static class ConditionedConfig {
        @Bean
        String foo() {
            return "bar";
        }
    }

    static final String BEAN = "foo";

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConditionedConfig.class);

    @Test
    void emptyProperties_false() {
        contextRunner.withPropertyValues(
                // no config
        ).run(context ->
                assertThat(context.containsBean(BEAN), is(false)));
    }

    @Test
    void withoutSubAttributes_false() {
        contextRunner.withPropertyValues(
                "root.sub=X"
        ).run(context ->
                assertThat(context.containsBean(BEAN), is(false)));
    }

    @Test
    void withSubAttributes_true() {
        contextRunner.withPropertyValues(
                "root.sub.attr1=A",
                "root.sub.attr2=B"
        ).run(context ->
                assertThat(context.containsBean(BEAN), is(true)));
    }
}
