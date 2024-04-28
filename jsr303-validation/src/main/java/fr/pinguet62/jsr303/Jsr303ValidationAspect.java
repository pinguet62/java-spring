package fr.pinguet62.jsr303;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * {@link Aspect} to validate all {@code jakarta.validation.*} (and custom extensions) annotations.
 * <p>
 * Throw an {@link ConstraintViolationException} containing all {@link ConstraintViolation}.
 *
 * @see <a href="https://www.jcp.org/en/jsr/detail?id=303">JSR-303</a>
 */
@Aspect
public class Jsr303ValidationAspect {

    @Before("execution(*.new(..)) && intoApplication() && hasAnnotatedArgument() && excludingThisAspect()")
    public void validateConstructorParameters(JoinPoint joinPoint) {
        Constructor<?> constructor = ConstructorSignature.class.cast(joinPoint.getSignature()).getConstructor();
        Object[] parameterValues = joinPoint.getArgs();
        Set<ConstraintViolation<Object>> theViolations = Validation.buildDefaultValidatorFactory().getValidator()
                .forExecutables().validateConstructorParameters(constructor, parameterValues);
        if (theViolations.size() > 0)
            throw new ConstraintViolationException(theViolations);
    }

    @Before("execution(* *(..)) && intoApplication() && hasAnnotatedArgument() && excludingThisAspect()")
    public void validateParameters(JoinPoint joinPoint) {
        Object object = joinPoint.getTarget();
        if (object == null)
            // static methods not supported
            return;
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] parameterValues = joinPoint.getArgs();
        Set<ConstraintViolation<Object>> theViolations = Validation.buildDefaultValidatorFactory().getValidator()
                .forExecutables().validateParameters(object, method, parameterValues);
        if (theViolations.size() > 0)
            throw new ConstraintViolationException(theViolations);
    }

    @AfterReturning(value = "intoApplication() && isMethodAnnotated() && excludingThisAspect()", returning = "returnValue")
    public void validateReturn(JoinPoint joinPoint, Object returnValue) {
        Object object = joinPoint.getTarget();
        if (object == null)
            // static methods not supported
            return;
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Set<ConstraintViolation<Object>> theViolations = Validation.buildDefaultValidatorFactory().getValidator()
                .forExecutables().validateReturnValue(object, method, returnValue);
        if (theViolations.size() > 0)
            throw new ConstraintViolationException(theViolations);
    }

    /**
     * Process only component of application.
     */
    @Pointcut("within(fr.pinguet62.jsr303..*)")
    public void intoApplication() {
    }

    /**
     * Support only:
     * <ul>
     * <li>Java EE API, from {@code jakarta.validation.constraints} package</li>
     * <li>Hibernate implementation, from {@code org.hibernate.validator} package</li>
     * </ul>
     */
    @Pointcut(/* constructor */ "execution(*.new(.., @(jakarta.validation.constraints..*) (*), ..)) || execution(*.new(.., @(org.hibernate.validator..*) (*), ..))"
            + " || "
            + /* method */ "execution(* *(.., @(jakarta.validation.constraints..*) (*), ..)) || execution(* *(.., @(org.hibernate.validator..*) (*), ..))")
    public void hasAnnotatedArgument() {
    }

    /**
     * Support only:
     * <ul>
     * <li>Java EE API, from {@code jakarta.validation.constraints} package</li>
     * <li>Hibernate implementation, from {@code org.hibernate.validator} package</li>
     * </ul>
     */
    @Pointcut("execution(@(jakarta.validation.constraints..*) * *.*(..)) || execution(@(org.hibernate.validator..*) * *.*(..))")
    public void isMethodAnnotated() {
    }

    /**
     * Fix error: don't process itself.
     */
    @Pointcut("!within(fr.pinguet62.jsr303.Jsr303ValidationAspect)")
    public void excludingThisAspect() {
    }
}
