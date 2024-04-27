package fr.pinguet62.springreactivekafka;

import fr.pinguet62.springreactivekafka.ReactiveKafkaListenerITTest.TestConfig;
import fr.pinguet62.springreactivekafka.ReactiveKafkaListenerITTest.TestConfig.TestService;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.EmbeddedKafkaKraftBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;
import java.util.Map.Entry;
import java.util.Optional;

import static fr.pinguet62.springreactivekafka.ReactiveKafkaListenerITTest.TOPIC_HEADERS;
import static fr.pinguet62.springreactivekafka.ReactiveKafkaListenerITTest.TOPIC_KEY;
import static fr.pinguet62.springreactivekafka.ReactiveKafkaListenerITTest.TOPIC_SPEL;
import static fr.pinguet62.springreactivekafka.ReactiveKafkaListenerITTest.TOPIC_VALUE;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_KEY;
import static org.springframework.kafka.support.KafkaHeaders.RECEIVED_TIMESTAMP;
import static org.springframework.kafka.test.EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS;
import static org.springframework.kafka.test.utils.KafkaTestUtils.producerProps;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.util.backoff.FixedBackOff.DEFAULT_INTERVAL;

@SpringBootTest(classes = TestConfig.class, properties = {
        "spring.kafka.bootstrap-servers=${" + SPRING_EMBEDDED_KAFKA_BROKERS + "}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.IntegerDeserializer", // default spring-kafka-test producer key-serializer
})
@EmbeddedKafka(topics = {TOPIC_VALUE, TOPIC_HEADERS, TOPIC_KEY, TOPIC_SPEL}, partitions = 2)
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD) // clean topics between tests
@ImportAutoConfiguration({
        ReactiveKafkaAutoConfiguration.class, // tested
        KafkaAutoConfiguration.class, // dependencies
})
public class ReactiveKafkaListenerITTest {

    public static final String TOPIC_VALUE = "topic-value";
    public static final String TOPIC_HEADERS = "topic-headers";
    public static final String TOPIC_KEY = "topic-key";
    public static final String TOPIC_SPEL = "topic-spel";

    public static final Duration TIMEOUT = Duration.ofMillis(3_000L/*fetch*/ + 5_000L/*commit*/ + 1_000L/*delta*/);

    @TestComponent
    static class TestConfig {
        @Service
        @AllArgsConstructor
        static class TestConsumer {
            TestService testService;

            @ReactiveKafkaListener(topic = TOPIC_VALUE, groupId = "handle-value")
            public Mono<Void> handleValue(String value) {
                return testService.doSomething(value);
            }

            @ReactiveKafkaListener(topic = TOPIC_HEADERS, groupId = "handle-spel")
            public Mono<Void> handleHeaders(@Header(RECEIVED_TIMESTAMP) String header) {
                return testService.doSomething(header);
            }

            @ReactiveKafkaListener(topic = TOPIC_KEY, groupId = "handle-key")
            public Mono<Void> handleKey(@Header(RECEIVED_KEY) int key) {
                return testService.doSomething(key);
            }

            @ReactiveKafkaListener(
                    topic = "${TOPIC_CONFIG:" + TOPIC_SPEL + "}",
                    groupId = "${GROUPID_CONFIG:handle-spel}",
                    properties = {"auto.offset.reset=${AUTOOFFSETRESET_CONFIG:earliest}"})
            public Mono<Void> handleSpEL(String value) {
                return testService.doSomething(value);
            }
        }

        interface TestService {
            Mono<Void> doSomething(Object obj);
        }
    }

    @Autowired
    EmbeddedKafkaKraftBroker embeddedKafkaBroker;

    @MockBean
    TestService subService;

    @Test
    void shouldExtractValue_byDefault() {
        PublisherProbe<Void> process = PublisherProbe.empty();
        when(subService.doSomething(any())).thenReturn(process.mono());

        String value = "Hello world!";

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, value));
            kafkaProducer.flush();
        }

        await().atMost(TIMEOUT).until(process::wasSubscribed);
        verify(subService).doSomething(value);
    }

    @Test
    void shouldExtractHeaders_whenParameterAnnotated() {
        PublisherProbe<Void> process = PublisherProbe.empty();
        when(subService.doSomething(any())).thenReturn(process.mono());

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_HEADERS, "any value"));
            kafkaProducer.flush();
        }

        await().atMost(TIMEOUT).until(process::wasSubscribed);
        verify(subService).doSomething(matches("\\d{13}"));
    }

    @Test
    void shouldExtractKey_whenParameterAnnotatedWithHeader() {
        PublisherProbe<Void> process = PublisherProbe.empty();
        when(subService.doSomething(any())).thenReturn(process.mono());

        int key = 1234;

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_KEY, key, "any value"));
            kafkaProducer.flush();
        }

        await().atMost(TIMEOUT).until(process::wasSubscribed);
        verify(subService).doSomething(key);
    }

    @Test
    void shouldSupportSpEL_inAnnotation() {
        PublisherProbe<Void> process = PublisherProbe.empty();
        when(subService.doSomething(any())).thenReturn(process.mono());

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_SPEL, "any value"));
            kafkaProducer.flush();
        }

        await().atMost(TIMEOUT).until(process::wasSubscribed);
    }

    @Test
    void shouldProcess_partitionItemsSequentially() throws Exception {
        TestPublisher<Void> process1 = TestPublisher.create();
        TestPublisher<Void> process2 = TestPublisher.create();
        when(subService.doSomething("first")).thenReturn(process1.mono());
        when(subService.doSomething("second")).thenReturn(process2.mono());

        int samePartition = 0;

        // 1st message
        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, samePartition, null, "first"));
            kafkaProducer.flush();
        }
        await().atMost(TIMEOUT).until(process1::wasSubscribed);

        // 2nd message
        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, samePartition, null, "second"));
            kafkaProducer.flush();
        }
        Thread.sleep(TIMEOUT);
        process2.assertWasNotSubscribed();

        process1.complete();

        await().atMost(TIMEOUT).until(process2::wasSubscribed);
    }

    @Test
    void shouldProcess_partitionsParallely() {
        TestPublisher<Void> process1 = TestPublisher.create();
        TestPublisher<Void> process2 = TestPublisher.create();
        when(subService.doSomething("first")).thenReturn(process1.mono());
        when(subService.doSomething("second")).thenReturn(process2.mono());

        // 1st partition
        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, 0, null, "first"));
            kafkaProducer.flush();
        }
        await().atMost(TIMEOUT).until(process1::wasSubscribed);

        // 2nd partition
        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, 1, null, "second"));
            kafkaProducer.flush();
        }
        await().atMost(TIMEOUT).until(process2::wasSubscribed);
    }

    @Test
    void shouldAcknowledgeMessage_atTheEnd_whenProcessSuccess() {
        PublisherProbe<Void> process1 = PublisherProbe.empty();
        when(subService.doSomething(any())).thenReturn(process1.mono());

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, "first"));
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, "second"));
            kafkaProducer.flush();
        }

        await().atMost(TIMEOUT).until(() -> getOffset("handle-value", TOPIC_VALUE).orElse(0L) == 2L);
    }

    @Test
    void shouldHandleError_whenProcessError_synchronouslyDuringInvocation() throws Exception {
        when(subService.doSomething(any()))
                .thenThrow(new RuntimeException("Oups!"))
                .thenThrow(new RuntimeException("I Did It Again"))
                .thenReturn(Mono.empty());

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, "any"));
            kafkaProducer.flush();
        }
        Thread.sleep((2 + 1/*security*/) * DEFAULT_INTERVAL); // minimal retry latency

        await().atMost(TIMEOUT).until(() -> getOffset("handle-value", TOPIC_VALUE).orElse(0L) == 1L);
        verify(subService, times(3)).doSomething(any()); // re-invoke annotated method
    }

    @Test
    void shouldRetry_whenProcessError_asynchronouslyInMono() throws Exception {
        when(subService.doSomething(any()))
                .thenReturn(Mono.error(new RuntimeException("Oups!")))
                .thenReturn(Mono.error(new RuntimeException("I Did It Again")))
                .thenReturn(Mono.empty());

        try (KafkaProducer<Integer, String> kafkaProducer = new KafkaProducer<>(producerProps(embeddedKafkaBroker))) {
            kafkaProducer.send(new ProducerRecord<>(TOPIC_VALUE, "any"));
            kafkaProducer.flush();
        }
        Thread.sleep((2 + 1/*security*/) * DEFAULT_INTERVAL); // minimal retry latency

        await().atMost(TIMEOUT).until(() -> getOffset("handle-value", TOPIC_VALUE).orElse(0L) == 1L);
        verify(subService, times(3)).doSomething(any()); // re-invoke annotated method
    }

    Optional<Long> getOffset(String groupId, String topic) {
        return embeddedKafkaBroker.doWithAdminFunction(admin -> {
            try {
                return admin.listConsumerGroupOffsets(groupId)
                        .partitionsToOffsetAndMetadata().get()
                        .entrySet().stream()
                        .findAny()
                        .filter(entry -> entry.getKey().topic().equals(topic))
                        .map(Entry::getValue)
                        .map(OffsetAndMetadata::offset);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
