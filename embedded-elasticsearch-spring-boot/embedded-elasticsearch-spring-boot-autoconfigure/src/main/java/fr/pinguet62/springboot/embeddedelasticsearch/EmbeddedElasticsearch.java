package fr.pinguet62.springboot.embeddedelasticsearch;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
public @interface EmbeddedElasticsearch {

    String version() default "";

    int port() default 9300;

    /**
     * Unit: {@link TimeUnit#MILLISECONDS}.
     *
     * @see pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic.Builder#startTimeoutInMs
     */
    int startTimeout() default 15_000;

    EmbeddedElasticsearchIndex[] indexes() default {};

}
