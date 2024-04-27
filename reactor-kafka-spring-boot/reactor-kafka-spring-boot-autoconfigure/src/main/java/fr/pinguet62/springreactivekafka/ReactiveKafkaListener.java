package fr.pinguet62.springreactivekafka;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD})
@Retention(RUNTIME)
public @interface ReactiveKafkaListener {

    String topic();

    String groupId();

    String[] properties() default {};
}
