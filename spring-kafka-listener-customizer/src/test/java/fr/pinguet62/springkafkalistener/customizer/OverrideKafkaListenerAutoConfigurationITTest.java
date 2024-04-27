package fr.pinguet62.springkafkalistener.customizer;

import fr.pinguet62.springkafkalistener.override.OverrideKafkaListenerAutoConfiguration;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;

import static fr.pinguet62.springkafkalistener.customizer.OverrideKafkaListenerAutoConfigurationITTest.EXPECTED_TOPIC;
import static fr.pinguet62.springkafkalistener.customizer.OverrideKafkaListenerAutoConfigurationITTest.SampleKafkaConsumer;
import static org.awaitility.Awaitility.await;
import static org.springframework.kafka.test.EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS;
import static org.springframework.kafka.test.utils.KafkaTestUtils.producerProps;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

@SpringBootTest(classes = SampleKafkaConsumer.class, properties = {
        "spring.kafka.bootstrap-servers=${" + SPRING_EMBEDDED_KAFKA_BROKERS + "}",
        "kafka.listeners.[public\\ void\\ fr.pinguet62.springkafkalistener.customizer.OverrideKafkaListenerAutoConfigurationITTest$SampleKafkaConsumer.listen()].topics=" + EXPECTED_TOPIC
})
@EmbeddedKafka(topics = EXPECTED_TOPIC, partitions = 1)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD) // clean topics between tests
@ImportAutoConfiguration({KafkaAutoConfiguration.class, KafkaListenerEndpointCustomizerAutoConfiguration.class, OverrideKafkaListenerAutoConfiguration.class})
class OverrideKafkaListenerAutoConfigurationITTest {

    public static final Duration TIMEOUT = Duration.ofMillis(3_000L/*fetch*/ + 5_000L/*commit*/ + 1_000L/*delta*/);

    static final String EXPECTED_TOPIC = "expected-topic";

    static int callCounter = 0;

    @TestComponent
    static class SampleKafkaConsumer {

        @KafkaListener(topics = "original-but-changed", groupId = "any")
        public void listen() {
            callCounter++;
        }
    }

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void shouldConsumeMessage_whereasOriginallyHeSaidHeWasListeningToAnotherTopic() {
        callCounter = 0;

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(EXPECTED_TOPIC, 1, "Pas cher !"));
            kafkaProducer.flush();
        }

        await().atMost(TIMEOUT).until(() -> callCounter == 1);
    }
}
