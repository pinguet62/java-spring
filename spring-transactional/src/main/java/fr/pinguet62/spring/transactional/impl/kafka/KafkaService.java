package fr.pinguet62.spring.transactional.impl.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaService {

    public static final String TOPIC = "sample";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ReactiveKafkaProducerTemplate<String, String> reactiveKafkaTemplate;

    public void sendValueSync(String value) {
        kafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), value);
    }

    public Mono<Void> sendValueSyncToAsync(String value) {
        return Mono.fromRunnable(() -> kafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), value));
    }

    public Mono<Void> sendValueAsync(String value) {
        return reactiveKafkaTemplate.send(TOPIC, UUID.randomUUID().toString(), value)
                .then();
    }
}
