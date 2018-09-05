package fr.pinguet62.test.springreactiveglobalcontext.reactorcontext;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactorStorageStorage {

    private static final Object KEY = ReactorStorageStorage.class.toString();

    public static Context withContext(String value) {
        return Context.of(KEY, value);
    }

    public static Mono<String> getValue() {
        return Mono.subscriberContext()
                .filter(it -> it.hasKey(KEY))
                .map(it -> it.get(KEY));
    }

    public static Mono<Context> setValue(String value) {
        return Mono.subscriberContext()
                .doOnNext(it -> it.put(KEY, value));
    }

}
