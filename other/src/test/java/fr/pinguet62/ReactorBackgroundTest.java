package fr.pinguet62;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import static fr.pinguet62.ReactorBackground.preSubscribe;

class ReactorBackgroundTest {

    @Nested
    class PreSubscribe {
        @Test
        void shouldTriggerSubscribeInBackGround_whenGlobalWrapperSubscribed() {
            TestPublisher<String> processPrefetchInBackground = TestPublisher.create();
            TestPublisher<String> mainProcess = TestPublisher.create();

            StepVerifier.create(
                            preSubscribe(
                                    processPrefetchInBackground.mono(),
                                    partialBackgroundResult ->
                                            mainProcess.mono().flatMap(mainResult ->
                                                    partialBackgroundResult.map(backgroundResult -> mainResult + backgroundResult))))
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
        void shouldCancelBackgroundSubscription_whenGlobalWrapperCanceled() {
            TestPublisher<String> background = TestPublisher.create();

            Disposable disposable = preSubscribe(
                    background.mono(),
                    partialBackgroundResult -> TestPublisher.create().mono() /*force wait*/)
                    .subscribe();

            disposable.dispose(); // cancel

            background.assertWasSubscribed();
            background.assertWasCancelled();
        }

        @Test
        void shouldCancelBackgroundSubscription_whenMainProcessTerminatedWithoutConsumingBackgroundTask() {
            TestPublisher<String> backgroundProcess = TestPublisher.create();

            StepVerifier.create(
                            preSubscribe(
                                    backgroundProcess.mono(),
                                    unusedPartialBackgroundResult -> Mono.just("Hello")))
                    .expectNext("Hello")
                    .verifyComplete();

            backgroundProcess.assertWasSubscribed();
            backgroundProcess.assertWasCancelled();
        }

        @Test
        void shouldEmitError_whenBackgroundProcessErrorEmitted() {
            TestPublisher<String> backgroundProcess = TestPublisher.create();

            StepVerifier.create(
                            preSubscribe(
                                    backgroundProcess.mono(),
                                    unusedPartialBackgroundResult -> TestPublisher.create().mono() /*force wait*/))
                    .then(() -> backgroundProcess.error(new RuntimeException("Oups!")))
                    .verifyErrorMessage("Oups!");
        }
    }
}
