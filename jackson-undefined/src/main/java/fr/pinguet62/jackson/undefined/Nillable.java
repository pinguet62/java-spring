package fr.pinguet62.jackson.undefined;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Nillable<T> {

    private final T value;

    private final boolean set;

    public static <T> Nillable<T> undefined() {
        return new Nillable<>(null, false);
    }

    public static <T> Nillable<T> ofDefined(T value) {
        return new Nillable<>(value, true);
    }

    private Nillable(T value, boolean set) {
        this.value = value;
        this.set = set;
    }

    public T get() {
        if (!set) {
            throw new NoSuchElementException("No value set");
        }
        return value;
    }

    public boolean isSet() {
        return set;
    }

    public boolean isUndefined() {
        return !set;
    }

    public void ifPresent(Consumer<? super T> action) {
        if (set) {
            action.accept(value);
        }
    }

    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (set) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    public <U> Nillable<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!set) {
            return undefined();
        } else {
            return ofDefined(mapper.apply(value));
        }
    }

    public T orElse(T other) {
        return set ? value : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        return set ? value : supplier.get();
    }

    public T orElseThrow() {
        if (!set) {
            throw new NoSuchElementException("No value set");
        }
        return value;
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (set) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Nillable)) {
            return false;
        }
        Nillable<?> other = (Nillable<?>) obj;
        return Objects.equals(set, other.set) && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(set, value);
    }

    @Override
    public String toString() {
        return value != null
                ? String.format("Nillable[%s]", value)
                : "Nillable.undefined";
    }
}
