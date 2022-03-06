package fr.pinguet62.test.springtransactional.impl;

import fr.pinguet62.test.springtransactional.OtherService;
import fr.pinguet62.test.springtransactional.impl.kafka.KafkaService;
import fr.pinguet62.test.springtransactional.impl.mongo.MongoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MainService {

    private final MongoService mongoService;
    private final KafkaService kafkaService;
    private final OtherService otherService;

    @Transactional
    public Mono<Void> processMongo(String value) {
        return mongoService.saveValue(value)
                .then(otherService.doFailing());
    }

    @Transactional
    public Mono<Void> processKafka(String value) {
        return kafkaService.sendValueAsync(value)
                .then(otherService.doFailing());
    }

    @Transactional
    public Mono<Void> processAll(String value) {
        return Mono.when(
                        mongoService.saveValue(value),
                        kafkaService.sendValueAsync(value))
                .then(otherService.doFailing());
    }

    public Mono<Void> processAllWithoutTransaction(String value) {
        return Mono.when(
                        mongoService.saveValue(value),
                        kafkaService.sendValueAsync(value))
                .then(otherService.doFailing());
    }
}
