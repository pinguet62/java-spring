package fr.pinguet62;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

// TODO doit être une lambda en paramètre, pour controler le cancel au niveau parent
public class ReactorBackground {

    public static <T> Function<Mono<T>, Mono<Mono<T>>> preSubscribe() {
        return parent -> {
            AtomicReference<MonoSink<T>> backgroundMonoSinkRef = new AtomicReference<>();
            AtomicReference<Disposable> disposableRef = new AtomicReference<>();
            Mono<T> wrapped = Mono.create(monoSink -> {
                System.out.println("Mono.create(MonoSink -> ...)");
                backgroundMonoSinkRef.set(monoSink);
                monoSink.onRequest(r -> {
                    System.out.println("MonoSink.onRequest(" + r + ")");
                });
                monoSink.onCancel(() -> {
                    System.out.println("MonoSink.onCancel()");
                });
                monoSink.onDispose(() -> {
                    System.out.println("MonoSink.onDispose()");
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
                    })
                    .doOnSuccess(it -> {
                        System.out.println("wrapper > .doOnSuccess()");
                    })
                    .doOnTerminate(() -> {
                        System.out.println("wrapper > .doOnTerminate()");
//                        disposableRef.get().dispose();
                    })
                    .doFinally(f -> {
                        System.out.println("wrapper > .doFinally(" + f + ")");
                    })
                    .doAfterTerminate(() -> {
                        System.out.println("wrapper > .doAfterTerminate()");
                    });
        };
    }
}
