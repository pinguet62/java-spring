# Spring & Reactor-Kafka consumer

`@ReactiveKafkaListener` Spring Boot annotation to consume data with https://projectreactor.io/docs/kafka/release/reference/.

## Usage

Exactly like native `@KafkaListener`, but method must return a `Publisher`.

```java
@Component
class MyConsumer {
    @ReactiveKafkaListener(topic = "sample", groupId = "my-consumer", properties = {})
    Mono<Void> consume(String value, @Header(RECEIVED_KEY) int key) {
        return myService.treat(message);
    }
}
```
