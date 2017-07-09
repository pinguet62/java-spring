# Test

## JSR-303 (Bean validation)

```java
@NotNull
@Size(min = 1)
List<@Email String> getEmails(@NotBlank String username) { /*...*/ }
```
