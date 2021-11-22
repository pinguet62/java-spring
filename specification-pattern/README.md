# Test

## Specification pattern

```java
Specification isBlack;
Specification priceGreaterThan10;
Specification typeAlcohol;

Specification spec = isBlack
    .and(priceGreaterThan10)
    .or(typeAlcohol);

boolean result = spec.isSatisfiedBy(product);
```
