# WireMock & Spring Boot Autoconfiguration

AutoConfiguration module for easy mock.

## Usage

1. `@WireMockApi` to declare new "host"
2. `@WireMockCallMock` to declare returned value from endpoint
3. Use `propertyKey` to provide/inject URL

```java
@WireMockApi(api = "facebook", propertyKey = "api.facebook.port")
class WireMockTest {
    @WireMockCallMock(api = "facebook", method = "GET", urlMatching = "/first", status = 200, body = "I'm first URL")
    void test() {
        restTemplate.getForEntity(environment.getProperty("api.facebook.port") + "/first", String.class).getBody(), is("I'm first URL");
    }
}
```
