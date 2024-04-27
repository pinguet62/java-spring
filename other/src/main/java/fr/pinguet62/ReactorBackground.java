package fr.pinguet62;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ReactorBackground {

    public static <T> Function<Mono<T>, Mono<Mono<T>>> preSubscribe() {
        return parent -> {
            AtomicReference<MonoSink<T>> backgroundMonoSinkRef = new AtomicReference<>();
            AtomicReference<Disposable> disposableRef = new AtomicReference<>();
            Mono<T> wrapped = Mono.create(monoSink -> {
                System.out.println("Mono.create(MonoSink -> ...)");
                backgroundMonoSinkRef.set(monoSink);
                monoSink.onCancel(() -> {
                    System.out.println("MonoSink.onCancel()");
                });
            });
            return Mono.just(wrapped)
                    .doOnSubscribe(s -> {
                        System.out.println("wrapper > .doOnSubscribe()");

                        Disposable disposable = parent.subscribe(
                                next -> {
                                    System.out.println("parent > next");
                                    backgroundMonoSinkRef.get().success(next);
                                },
                                throwable -> {
                                    System.out.println("parent > throwable");
                                    backgroundMonoSinkRef.get().error(throwable);
                                });
                        disposableRef.set(disposable);
                    })
                    .doOnCancel(() -> {
                        System.out.println("wrapper > .doOnCancel()");
                        disposableRef.get().dispose();
                    });
        };
    }

    /**
     * The {@code Mono<T>} must be consumed tardily to avoid blocking process.
     */
    public static <T, O> Mono<O> withBackground(Mono<T> task, Function<Mono<T>, Mono<O>> mapper) {
        return Mono.empty();
    }
}
