# Spring `@KafkaListener` customizer

Customize parameters of `@KafkaListener` dynamically from properties.

## Usage

```properties
kafka.listeners.<metoh-name>.topics=...
kafka.listeners.<metoh-name>.groupId=...
kafka.listeners.<metoh-name>.properties=...
```

```java
@Component
class MyConsumer {
    @KafkaListener(topic = "original", groupId = "my-original-consumer", properties = {})
    void consume(String value) {
        // ...
    }
}
```
