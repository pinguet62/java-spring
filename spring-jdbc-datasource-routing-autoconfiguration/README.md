# JDBC & Datasource routing (Spring Boot Autoconfiguration)

```properties
spring.datasource.first.url=jdbc:h2:mem:foo
spring.datasource.second.url=jdbc:h2:mem:bar
```

```java
RoutingDataSourceLookupHolder.set("first");
sharedRepository.save(entity); // will query "jdbc:h2:mem:foo"
```
