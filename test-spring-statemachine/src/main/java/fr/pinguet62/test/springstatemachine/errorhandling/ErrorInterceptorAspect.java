package fr.pinguet62.test.springstatemachine.errorhandling;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Aspect
public class ErrorInterceptorAspect {

    /** {@link Deque} who take all {@link Exception} of current {@link Thread}. */
    private final static ThreadLocal<Deque<Throwable>> HOLDER = new ThreadLocal<>();

    /**
     * <b>Convention:</b> {@link Action} & {@link Guard} calls are proxied, and contains only 1 {@link StateContext} argument.
     */
    @AfterThrowing(pointcut = "execution(* fr.pinguet62.test.springstatemachine..*.*(org.springframework.statemachine.StateContext))", throwing = "ex")
    public void interceptActionAndGuardException(Throwable ex) throws Throwable {
        Deque<Throwable> exceptions = HOLDER.get();

        // not enabled
        if (exceptions == null)
            return;

        exceptions.add(ex);
    }

    /** Used by {@link Aspect} to re-throw any {@link Exception} occurred into {@link StateMachine}. */
    @Target({ METHOD })
    @Retention(RUNTIME)
    public static @interface InterceptInternalStateMachineErrors {
    }

    @Around("@annotation(annotation)")
    public Object throwExceptionIfErrorEncountered(ProceedingJoinPoint proceedingJoinPoint,
            InterceptInternalStateMachineErrors annotation) throws Throwable {
        Deque<Throwable> exceptions = HOLDER.get();

        // init interceptor chain
        if (exceptions == null) {
            exceptions = new LinkedList<>();
            HOLDER.set(exceptions);
        }

        int previousErrorsCount = exceptions.size();

        Object result = proceedingJoinPoint.proceed();

        if (exceptions.size() == previousErrorsCount)
            return result;
        else {
            List<Throwable> nextExceptions = new ArrayList<>();
            while (exceptions.size() > previousErrorsCount + 1)
                nextExceptions.add(0, exceptions.removeLast());
            throw new InternalStateMachineException(exceptions.getLast(), nextExceptions);
        }
    }

}