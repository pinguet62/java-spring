package fr.pinguet62.mongo.embedded;

import de.flapdoodle.embed.mongo.config.MongodConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @see MongodConfig#args()
 */
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(EmbeddedMongodbArgs.class)
@Import(EmbeddedMongodbArgsImporter.class)
public @interface EmbeddedMongodbArg {

    String key();

    String value() default "";
}
