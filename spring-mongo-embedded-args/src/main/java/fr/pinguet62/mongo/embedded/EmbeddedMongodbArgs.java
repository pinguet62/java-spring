package fr.pinguet62.mongo.embedded;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Import(EmbeddedMongodbArgsImporter.class)
public @interface EmbeddedMongodbArgs {

    EmbeddedMongodbArg[] value();
}
