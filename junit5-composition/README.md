# JUnit5 & combination sources

Create a **matrice** with all provided values (`@ValueSource`, ...).

```java
@ParameterizedTest
@CombinationSources({
        @CombinationSource(
                valueSource = @ValueSource(strings = {"Scooter", "Car"}),
                emptySource = @EmptySource),
        @CombinationSource(
                valueSource = @ValueSource(ints = {14, 16}),
                nullSource = @NullSource),
        @CombinationSource(
                valueSource = @ValueSource(booleans = {false, true})),
})
void test(String vehicle, Integer age, boolean sex) {
    // ...
}
```
