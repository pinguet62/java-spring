package fr.pinguet62.test.junit5.composition;

import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface CombinationSource {

    ValueSource[] valueSource() default {};

    EmptySource[] emptySource() default {};

    NullSource[] nullSource() default {};

    EnumSource[] enumSource() default {};
}
