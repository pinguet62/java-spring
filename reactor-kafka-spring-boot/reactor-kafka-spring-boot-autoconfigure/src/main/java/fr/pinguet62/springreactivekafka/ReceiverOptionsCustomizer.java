package fr.pinguet62.springreactivekafka;

import reactor.kafka.receiver.ReceiverOptions;

public interface ReceiverOptionsCustomizer<K, V> {

    ReceiverOptions<K, V> customize(ReceiverOptions<K, V> senderOptions);
}
