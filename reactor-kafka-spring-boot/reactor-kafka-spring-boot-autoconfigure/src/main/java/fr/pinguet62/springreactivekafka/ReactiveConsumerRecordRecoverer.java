package fr.pinguet62.springreactivekafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.function.BiFunction;

@FunctionalInterface
public interface ReactiveConsumerRecordRecoverer extends BiFunction<ConsumerRecord<?, ?>, Throwable, Boolean> {
}
