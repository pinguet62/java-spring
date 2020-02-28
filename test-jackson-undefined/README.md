# Test

## Jackson "undefined"

Problem: `undefined` and `null` are handled as `null` by Java.

### Solution 1: intercept *setter* and store "not undefined" state in *specific flag*

Example:
```java
public class MyDto {
    @Getter
    private boolean valueSet = false;
    private String value; // default: null

    public void setValue(String value) {
        valueSet = true;
        this.value = value;
    }

    public String getValue() {
        if (!valueSet) throw new NoSuchElementException("No value present");
        return value;
    }
}

// Usage
void pathResource(MyEntity entity, MyDto dto) {
    if (dto.isValueSet()) entity.setValue(dto.getValue());
}
```

Problem: **verbose** because each setter must be wrapped.

### Solution 2: use nullable `Optional`

Example:
```java
public class MyDto {
    @Getter
    @Setter
    private Optional<String> value; // default: null

    public void setValue(Optional<String> value) {
        this.value = value;
    }
}

// Usage
void pathResource(MyEntity entity, MyDto dto) {
    if (dto.getValue() != null) entity.setValue(dto.getValue().get());
}
```

Problem: **bad practice** because `Optional` should never be `null`.

### Solution 3: wrap type around "nillable" (like `Optional`)

Example:
```java
public class MyDto {
    @Getter
    private Nillable<String> value = Nillable.undefined();
}

// Usage
void pathResource(MyEntity entity, MyDto dto) {
    if (dto.getValue().isSet() != null) entity.setValue(dto.getValue().get());
}
```
