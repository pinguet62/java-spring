package fr.pinguet62.spring.transactional;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class OtherService {

    public static class OtherException extends RuntimeException {
    }

    public void doFailingSync() {
        throw new OtherException();
    }

    public Mono<Void> doFailing() {
        return Mono.error(new OtherException());
    }
}
