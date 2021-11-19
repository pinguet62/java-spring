package fr.pinguet62.test.springstatemachine.errorhandling;

import java.util.Collection;

public class InternalStateMachineException extends RuntimeException {

    private static final long serialVersionUID = 1;

    private final Collection<Throwable> nextThrowable;

    /**
     * @param firstCause    The first {@link Throwable} occurred.
     * @param nextThrowable Next {@link Throwable} occurred after the {@code firstCause}.
     */
    public InternalStateMachineException(Throwable firstCause, Collection<Throwable> nextThrowable) {
        super(firstCause);
        this.nextThrowable = nextThrowable;
    }

    Collection<Throwable> getNextThrowable() {
        return nextThrowable;
    }
}
