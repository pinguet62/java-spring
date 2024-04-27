package fr.pinguet62.springreactivekafka;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.util.backoff.FixedBackOff;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.util.retry.Retry;

import java.time.Duration;

@RequiredArgsConstructor
@CommonsLog
public class ReactiveCommonErrorHandler {

    @NonNull
    private final ReactiveConsumerRecordRecoverer consumerRecordRecoverer;
    @NonNull
    private final FixedBackOff fixedBackOff;

    public Retry getRetry(ReceiverRecord<?, ?> record) {
        return Retry
                .backoff(fixedBackOff.getMaxAttempts(), Duration.ofMillis(fixedBackOff.getInterval()))
                .filter(error -> consumerRecordRecoverer.apply(record, error))
                .doBeforeRetry(retrySignal -> log.warn("Error handler threw an exception", retrySignal.failure()));
    }
}
