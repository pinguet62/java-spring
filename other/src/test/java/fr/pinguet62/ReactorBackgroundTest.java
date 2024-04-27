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

class ReactorBackgroundTest {

    @Nested
    class PreSubscribe {
        @Test
        void shouldTriggerSubscribeInBackGround() {
            TestPublisher<String> processPrefetchInBackground = TestPublisher.create();

            TestPublisher<String> mainProcess = TestPublisher.create();

            StepVerifier.create(
                            processPrefetchInBackground.mono()
                                    .transform(preSubscribe())
                                    .flatMap(background ->
                                            mainProcess.mono().flatMap(first ->
                                                    background.map(second -> first + second))))
                    .then(() -> {
                        mainProcess.assertWasSubscribed();
                        processPrefetchInBackground.assertWasSubscribed(); // launched in background

                        mainProcess.emit("Hello");
                    })
                    .then(() -> processPrefetchInBackground.emit(" world!"))
                    .expectNext("Hello world!")
                    .verifyComplete();
        }

        @Test
        void shouldCancelSubscriptionWhenParentCanceled() {
            TestPublisher<String> background = TestPublisher.create();

            Disposable disposable = background.mono()
                    .transform(preSubscribe())
                    .flatMap(asynctask -> TestPublisher.create().mono() /*force wait*/)
                    .subscribe();
            disposable.dispose(); // cancel

            background.assertWasSubscribed();
            background.assertWasCancelled();
        }

        @Test
        void shouldCancelSubscription_whenMainProcessTerminatedWithoutConsumingBackgroundTask() {
            TestPublisher<String> backgroundProcess = TestPublisher.create();

            StepVerifier.create(
                            backgroundProcess.mono()
                                    .transform(preSubscribe())
                                    .flatMap(background -> Mono.just("Hello")))
                    .expectNext("Hello")
                    .verifyComplete();

            backgroundProcess.assertWasSubscribed();
            backgroundProcess.assertWasCancelled();
        }

        @Disabled
        @Test
        void error_DirectOrLazy() {
            fail();
        }

        @Disabled
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

        @Test
        void test2() {
            Mono.just("A")
                    .doOnSubscribe(x -> System.out.println("A doOnSubscribe()"))
                    .doOnCancel(() -> System.out.println("A doOnCancel()"))
                    .doOnSuccess(x -> System.out.println("A doOnSuccess()"))
                    .doOnTerminate(() -> System.out.println("A doOnTerminate()"))

                    .map(it -> it)
                    .doOnNext(x -> System.out.println("doOnNext()"))
                    .doOnSubscribe(x -> System.out.println("B doOnSubscribe()"))
                    .doOnCancel(() -> System.out.println("B doOnCancel()"))
                    .doOnSuccess(x -> System.out.println("B doOnSuccess()"))
                    .doOnTerminate(() -> System.out.println("B doOnTerminate()"))

                    .block();
        }
    }
}
