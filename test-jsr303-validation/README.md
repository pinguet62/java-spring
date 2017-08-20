# Test

## JSR-303 (Bean validation)

[AspectJ](http://www.eclipse.org/aspectj) `@Aspect` for constructor + method + returned value **validation** based on [**JSR 303**](https://jcp.org/en/jsr/detail?id=303) annotations.

```java
@NotNull
@Size(min = 1)
List<@Email String> getEmails(@NotBlank String username) { /*...*/ }
```
