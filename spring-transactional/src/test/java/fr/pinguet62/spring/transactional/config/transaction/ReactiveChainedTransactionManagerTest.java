package fr.pinguet62.spring.transactional.config.transaction;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.*;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.reactive.TransactionContextManager;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.publisher.TestPublisher;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.transaction.HeuristicCompletionException.STATE_MIXED;
import static org.springframework.transaction.HeuristicCompletionException.STATE_ROLLED_BACK;

@ExtendWith(MockitoExtension.class)
class ReactiveChainedTransactionManagerTest {

    @Test
    void shouldDeclareAtLeastOneDelegate() {
        assertThrows(IllegalArgumentException.class, () -> new ReactiveChainedTransactionManager(/*empty*/));
    }

    @Nested
    class getReactiveTransaction {
        @Test
        void shouldInitializeAllRegisteredTransactionManagersInOrder() {
            TransactionDefinition definition = new DefaultTransactionAttribute();

            ReactiveTransactionManager firstTransactionManager = mock(ReactiveTransactionManager.class);
            ReactiveTransaction firstTransaction = mock(ReactiveTransaction.class);
            when(firstTransactionManager.getReactiveTransaction(definition)).thenReturn(Mono.just(firstTransaction));

            ReactiveTransactionManager secondTransactionManager = mock(ReactiveTransactionManager.class);
            ReactiveTransaction secondTransaction = mock(ReactiveTransaction.class);
            when(secondTransactionManager.getReactiveTransaction(definition)).thenReturn(Mono.just(secondTransaction));

            ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(firstTransactionManager, secondTransactionManager);

            StepVerifier.create(transactionManager.getReactiveTransaction(definition)
                            // invoked by TransactionAspectSupport
                            .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                    .expectNextMatches(it -> it instanceof ReactiveMultiTransactionStatus reactiveMultiTransactionStatus
                            && reactiveMultiTransactionStatus.getTransactionStatuses().get(firstTransactionManager) == firstTransaction
                            && reactiveMultiTransactionStatus.getTransactionStatuses().get(secondTransactionManager) == secondTransaction)
                    .verifyComplete();
        }

        @Nested
        class failed {
            @ParameterizedTest
            @ArgumentsSource(EmptyOrError.class)
            void shouldThrowExceptionAfterRollbackOfPreviousTransactions(Mono<Void> emptyOrError) {
                ReactiveTransactionManager firstTransactionManager = mock(ReactiveTransactionManager.class);
                when(firstTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                // then rollback-ed
                when(firstTransactionManager.rollback(any())).thenReturn(emptyOrError);

                ReactiveTransactionManager secondTransactionManager = mock(ReactiveTransactionManager.class);
                when(secondTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                // then rollback-ed
                when(secondTransactionManager.rollback(any())).thenReturn(emptyOrError);

                ReactiveTransactionManager thirdTransactionManager = mock(ReactiveTransactionManager.class);
                Throwable transactionError = new RuntimeException();
                when(thirdTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.error(transactionError));

                ReactiveTransactionManager fourthTransactionManager = mock(ReactiveTransactionManager.class);
                // not called

                ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(firstTransactionManager, secondTransactionManager, thirdTransactionManager, fourthTransactionManager);

                StepVerifier.create(transactionManager.getReactiveTransaction(new DefaultTransactionAttribute())
                                // invoked by TransactionAspectSupport
                                .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                        .verifyErrorMatches(throwable ->
                                throwable instanceof CannotCreateTransactionException cannotCreateTransactionException
                                        && cannotCreateTransactionException.getCause() == transactionError);
            }
        }
    }

    @Nested
    class commit {
        @Test
        void shouldCommitAllRegisteredTransactionManagersInReverseOrder() {
            TransactionDefinition definition = new DefaultTransactionAttribute();

            ReactiveTransactionManager firstTransactionManager = mock(ReactiveTransactionManager.class);
            ReactiveTransaction firstTransaction = mock(ReactiveTransaction.class);
            when(firstTransactionManager.getReactiveTransaction(definition)).thenReturn(Mono.just(firstTransaction));
            TestPublisher<Void> firstCommit = TestPublisher.create();
            when(firstTransactionManager.commit(firstTransaction)).thenReturn(firstCommit.mono());

            ReactiveTransactionManager secondTransactionManager = mock(ReactiveTransactionManager.class);
            ReactiveTransaction secondTransaction = mock(ReactiveTransaction.class);
            when(secondTransactionManager.getReactiveTransaction(definition)).thenReturn(Mono.just(secondTransaction));
            TestPublisher<Void> secondCommit = TestPublisher.create();
            when(secondTransactionManager.commit(secondTransaction)).thenReturn(secondCommit.mono());

            ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(firstTransactionManager, secondTransactionManager);

            StepVerifier.create(transactionManager.getReactiveTransaction(definition)
                            .flatMap(transaction -> transactionManager.commit(transaction))
                            // invoked by TransactionAspectSupport
                            .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                    .then(() -> {
                        firstCommit.assertWasNotSubscribed();
                        secondCommit.assertWasSubscribed();
                        secondCommit.complete();
                    })
                    .then(() -> {
                        firstCommit.assertWasSubscribed();
                        firstCommit.complete();
                    })
                    .verifyComplete();
        }

        @Nested
        class failed {
            @Test
            void shouldThrowRolledBackException_forSingleTM() {
                ReactiveTransactionManager singleTransactionManager = mock(ReactiveTransactionManager.class);
                when(singleTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                Throwable rollbackError = new RuntimeException();
                when(singleTransactionManager.commit(any())).thenReturn(Mono.error(rollbackError)); // fails

                ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(singleTransactionManager);

                StepVerifier.create(transactionManager.getReactiveTransaction(new DefaultTransactionAttribute())
                                .flatMap(transaction -> transactionManager.commit(transaction))
                                // invoked by TransactionAspectSupport
                                .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                        .verifyErrorMatches(throwable ->
                                throwable instanceof HeuristicCompletionException heuristicCompletionException
                                        && heuristicCompletionException.getOutcomeState() == STATE_ROLLED_BACK
                                        && heuristicCompletionException.getCause() == rollbackError);
            }

            @ParameterizedTest
            @ArgumentsSource(EmptyOrError.class)
            void shouldThrowMixedRolledBackException_forNonFirstTMFailure(Mono<Void> emptyOrError) {
                ReactiveTransactionManager thirdTransactionManager = mock(ReactiveTransactionManager.class);
                when(thirdTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                when(thirdTransactionManager.commit(any())).thenReturn(Mono.empty()); // success

                ReactiveTransactionManager secondTransactionManager = mock(ReactiveTransactionManager.class);
                when(secondTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                Throwable rollbackError = new RuntimeException();
                when(secondTransactionManager.commit(any())).thenReturn(Mono.error(rollbackError)); // fails

                ReactiveTransactionManager firstTransactionManager = mock(ReactiveTransactionManager.class);
                when(firstTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                when(firstTransactionManager.rollback(any())).thenReturn(emptyOrError); // then rollback-ed

                ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(firstTransactionManager, secondTransactionManager, thirdTransactionManager);

                StepVerifier.create(transactionManager.getReactiveTransaction(new DefaultTransactionAttribute())
                                .flatMap(transaction -> transactionManager.commit(transaction))
                                // invoked by TransactionAspectSupport
                                .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                        .verifyErrorMatches(throwable ->
                                throwable instanceof HeuristicCompletionException heuristicCompletionException
                                        && heuristicCompletionException.getOutcomeState() == STATE_MIXED
                                        && heuristicCompletionException.getCause() == rollbackError);
            }
        }
    }

    @Nested
    class rollback {
        @Test
        void shouldRollbackAllRegisteredTransactionManagersInReverseOrder() {
            TransactionDefinition definition = new DefaultTransactionAttribute();

            ReactiveTransactionManager firstTransactionManager = mock(ReactiveTransactionManager.class);
            ReactiveTransaction firstTransaction = mock(ReactiveTransaction.class);
            when(firstTransactionManager.getReactiveTransaction(definition)).thenReturn(Mono.just(firstTransaction));
            TestPublisher<Void> firstRollback = TestPublisher.create();
            when(firstTransactionManager.rollback(firstTransaction)).thenReturn(firstRollback.mono());

            ReactiveTransactionManager secondTransactionManager = mock(ReactiveTransactionManager.class);
            ReactiveTransaction secondTransaction = mock(ReactiveTransaction.class);
            when(secondTransactionManager.getReactiveTransaction(definition)).thenReturn(Mono.just(secondTransaction));
            TestPublisher<Void> secondRollback = TestPublisher.create();
            when(secondTransactionManager.rollback(secondTransaction)).thenReturn(secondRollback.mono());

            ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(firstTransactionManager, secondTransactionManager);

            StepVerifier.create(transactionManager.getReactiveTransaction(definition)
                            .flatMap(transaction -> transactionManager.rollback(transaction))
                            // invoked by TransactionAspectSupport
                            .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                    .then(() -> {
                        firstRollback.assertWasNotSubscribed();
                        secondRollback.assertWasSubscribed();
                        secondRollback.complete();
                    })
                    .then(() -> {
                        firstRollback.assertWasSubscribed();
                        firstRollback.complete();
                    })
                    .verifyComplete();
        }

        @Nested
        class failed {
            @Test
            void shouldThrowException_forSingleTM() {
                ReactiveTransactionManager singleTransactionManager = mock(ReactiveTransactionManager.class);
                when(singleTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                Throwable rollbackError = new RuntimeException();
                when(singleTransactionManager.rollback(any())).thenReturn(Mono.error(rollbackError)); // fails

                ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(singleTransactionManager);

                StepVerifier.create(transactionManager.getReactiveTransaction(new DefaultTransactionAttribute())
                                .flatMap(transaction -> transactionManager.rollback(transaction))
                                // invoked by TransactionAspectSupport
                                .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                        .verifyErrorMatches(throwable ->
                                throwable instanceof UnexpectedRollbackException unexpectedRollbackException
                                        && unexpectedRollbackException.getCause() == rollbackError);
            }

            @ParameterizedTest
            @ArgumentsSource(EmptyOrError.class)
            void shouldThrowException_forNonFirstTMFailure(Mono<Void> emptyOrError) {
                ReactiveTransactionManager thirdTransactionManager = mock(ReactiveTransactionManager.class);
                when(thirdTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                when(thirdTransactionManager.rollback(any())).thenReturn(Mono.empty()); // success

                ReactiveTransactionManager secondTransactionManager = mock(ReactiveTransactionManager.class);
                when(secondTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                Throwable rollbackError = new RuntimeException();
                when(secondTransactionManager.rollback(any())).thenReturn(Mono.error(rollbackError)); // fails

                ReactiveTransactionManager firstTransactionManager = mock(ReactiveTransactionManager.class);
                when(firstTransactionManager.getReactiveTransaction(any())).thenReturn(Mono.just(mock(ReactiveTransaction.class)));
                when(firstTransactionManager.rollback(any())).thenReturn(emptyOrError); // try rollback

                ReactiveChainedTransactionManager transactionManager = new ReactiveChainedTransactionManager(firstTransactionManager, secondTransactionManager, thirdTransactionManager);

                StepVerifier.create(transactionManager.getReactiveTransaction(new DefaultTransactionAttribute())
                                .flatMap(transaction -> transactionManager.rollback(transaction))
                                // invoked by TransactionAspectSupport
                                .contextWrite(TransactionContextManager.getOrCreateContext()).contextWrite(TransactionContextManager.getOrCreateContextHolder()))
                        .verifyErrorMatches(throwable ->
                                throwable instanceof UnexpectedRollbackException unexpectedRollbackException
                                        && unexpectedRollbackException.getCause() == rollbackError);
            }
        }
    }

    static class EmptyOrError implements ArgumentsProvider {
        @Override
        public Stream<Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(Mono.empty()),
                    Arguments.of(Mono.error(new RuntimeException("failed"))));
        }
    }
}
