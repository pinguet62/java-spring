package fr.pinguet62.flapdoodle.mongo.args;

import de.flapdoodle.embed.mongo.commands.ImmutableMongodArguments;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @see ImmutableMongodArguments#args()
 */
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(EmbeddedMongodbArgs.class)
@Import(EmbeddedMongodbArgsImporter.class)
public @interface EmbeddedMongodbArg {

    String key();

    String value() default "";
}
