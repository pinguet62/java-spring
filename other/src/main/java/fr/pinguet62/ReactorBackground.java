package fr.pinguet62;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

// TODO doit être une lambda en paramètre, pour controler le cancel au niveau parent
public class ReactorBackground {

    public static <O, B> Mono<O> preSubscribe(Mono<B> task, Function<Mono<B>, Mono<O>> mapper) {
        AtomicReference<MonoSink<B>> wrapperMonoSinkRef = new AtomicReference<>();
        AtomicReference<Disposable> backgroundDisposableRef = new AtomicReference<>();

        Mono<B> prefetch = Mono.<B>create(monoSink -> {
                    System.out.println("Mono.create(MonoSink -> ...)");
                    wrapperMonoSinkRef.set(monoSink);
                    monoSink.onRequest(r -> {
                        System.out.println("Mono.create > MonoSink.onRequest(" + r + ")");
                    });
                    monoSink.onCancel(() -> {
                        System.out.println("Mono.create > MonoSink.onCancel()");
                    });
                    monoSink.onDispose(() -> {
                        System.out.println("Mono.create > MonoSink.onDispose()");
                    });
                })
                .doOnSubscribe(s -> {
                    System.out.println("Mono.create > .doOnSubscribe()");
                });

        Mono<O> wrapper = mapper.apply(prefetch);

        return wrapper
                .doOnSubscribe(s -> {
                    System.out.println("preSubscribe wrapper > .doOnSubscribe()");
                    System.out.println("background > .subscribe()");
                    Disposable disposable = task.subscribe(
                            next -> {
                                System.out.println("background > next");
                                wrapperMonoSinkRef.get().success(next);
                            },
                            throwable -> {
                                System.out.println("background > throwable");
                                wrapperMonoSinkRef.get().error(throwable);
                            });
                    System.out.println("preSubscribe > .doOnSubscribe() > save Disposable");
                    backgroundDisposableRef.set(disposable);
                })
                .doOnCancel(() -> {
                    System.out.println("preSubscribe wrapper > .doOnCancel()");
                    backgroundDisposableRef.get().dispose();
                })
                .doOnTerminate(() -> {
                    System.out.println("preSubscribe wrapper > .doOnTerminate()");
                    backgroundDisposableRef.get().dispose();
                });
    }
}
