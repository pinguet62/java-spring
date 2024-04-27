package fr.pinguet62.test.junit5.composition;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
@ArgumentsSource(CombinationArgumentsProvider.class)
public @interface CombinationSources {

    CombinationSource[] value();
}
