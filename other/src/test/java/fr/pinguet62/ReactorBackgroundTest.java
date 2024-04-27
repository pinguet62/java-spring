package fr.pinguet62;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.util.List;

import static fr.pinguet62.ReactorBackground.preSubscribe;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReactorBackgroundTest {

    @Nested
    class PreSubscribe {
        @Test
        void shouldTriggerSubscribeInBackGround() {
            TestPublisher<String> processPrefetchInBackground = TestPublisher.create();

            TestPublisher<String> mainProcess = TestPublisher.create();
            Runnable intermediateProcess = mock(Runnable.class);

            StepVerifier.create(Mono.zip(
                                    processPrefetchInBackground.mono()
                                            .transform(preSubscribe()),
                                    mainProcess.mono())
                            .flatMap(tuple -> {
                                intermediateProcess.run();
                                String first = tuple.getT2();
                                Mono<String> back = tuple.getT1();
                                return back.map(second -> first + second);
                            }))
                    .then(() -> {
                        mainProcess.assertWasSubscribed();
                        processPrefetchInBackground.assertWasSubscribed(); // launched in background

                        mainProcess.emit("Hello");
                    })
                    .then(() -> {
                        verify(intermediateProcess).run(); // not blocking

                        processPrefetchInBackground.emit(" world!");
                    })
                    .expectNext("Hello world!")
                    .verifyComplete();
        }

        @Test
        void shouldCancelSubscriptionWhenParentCanceled() {
            TestPublisher<String> background = TestPublisher.create();

            Disposable disposable = background.mono()
                    .transform(preSubscribe())
                    .flatMap(x -> TestPublisher.create().mono() /*force wait*/)
                    .subscribe();
            disposable.dispose(); // cancel

            background.assertWasSubscribed();
            background.assertWasCancelled();
        }

        @Test
        void shouldCancelSubscriptionWhenNotUsedInSubprocess() {
            TestPublisher<String> background = TestPublisher.create();

            StepVerifier.create(Mono.zip(
                                    background.mono()
                                            .transform(preSubscribe()),
                                    Mono.just("Hello"))
                            .map(tuple -> {
                                String first = tuple.getT2();
                                // no usage: tuple.getT1();
                                return first;
                            }))
                    .expectNext("Hello")
                    .verifyComplete();

            background.assertWasSubscribed();
            background.assertWasCancelled();
        }

        @Disabled
        @Test
        void error_DirectOrLazy() {
            fail();
        }

        @Test
        void test() {
            for (Mono<String> mono : List.of(
                    Mono.just("A"),
                    Mono.<String>empty())) {
                System.out.println("-----");
                StepVerifier.create(
                                Mono.zip(
                                                mono,
                                                Mono.just("B")
                                                        .doOnSubscribe(x -> System.out.println("B doOnSubscribe()"))
                                                        .doOnCancel(() -> System.out.println("B doOnCancel()"))
                                                        .doOnSuccess(x -> System.out.println("B doOnSuccess()"))
                                                        .doOnTerminate(() -> System.out.println("B doOnTerminate()")))
                                        .doOnNext(x -> System.out.println("process..."))
                                        .doOnSubscribe(x -> System.out.println("zip() doOnSubscribe()"))
                                        .doOnCancel(() -> System.out.println("zip() doOnCancel()"))
                                        .doOnSuccess(x -> System.out.println("zip() doOnSuccess()"))
                                        .doOnTerminate(() -> System.out.println("zip() doOnTerminate()"))
                        )
                        .expectNextMatches(x -> true)
                        .verifyComplete();
            }
        }
    }
}
