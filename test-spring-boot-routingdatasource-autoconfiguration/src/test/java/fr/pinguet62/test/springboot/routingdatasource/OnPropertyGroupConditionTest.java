package fr.pinguet62.test.springboot.routingdatasource;

import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OnPropertyGroupConditionTest {

    @Configuration
    @ConditionalOnPropertyGroup("root.sub")
    protected static class ConditionedConfig {
        @Bean
        public String foo() {
            return "bar";
        }
    }

    private static final String BEAN = "foo";

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConditionedConfig.class);

    @Test
    public void emptyProperties_false() {
        contextRunner.withPropertyValues(
                // no config
        ).run(context ->
                assertThat(context.containsBean(BEAN), is(false))
        );
    }

    @Test
    public void withoutSubAttributes_false() {
        contextRunner.withPropertyValues(
                "root.sub=X"
        ).run(context ->
                assertThat(context.containsBean(BEAN), is(false))
        );
    }

    @Test
    public void withSubAttributes_true() {
        contextRunner.withPropertyValues(
                "root.sub.attr1=A",
                "root.sub.attr2=B"
        ).run(context ->
                assertThat(context.containsBean(BEAN), is(true))
        );
    }

}
