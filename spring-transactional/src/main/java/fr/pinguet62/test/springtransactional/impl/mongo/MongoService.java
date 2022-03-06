package fr.pinguet62.test.springtransactional.impl.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MongoService {

    private final SampleRepository repository;

    public Mono<Void> saveValue(String value) {
        return repository.save(new SampleDocument(value))
                .then();
    }
}
