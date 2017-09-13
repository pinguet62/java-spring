package fr.pinguet62.test.jsr303;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * {@link Aspect} to validate all {@code javax.validation.*} (and custom extensions) annotations.
 * <p>
 * Throw an {@link ConstraintViolationException} containing all {@link ConstraintViolation}.
 * <p>
 * {@code "!within()"} to don't proces {@link Aspect} it-self.
 *
 * @see <a href="https://www.jcp.org/en/jsr/detail?id=303">JSR-303</a>
 */
@Aspect
public class Jsr303ValidationAspect {

    @Before("execution(fr.pinguet62.test.jsr303..*.new(..))"
            // fix
            + " && !within(fr.pinguet62.test.jsr303.Jsr303ValidationAspect)")
    public void validateConstructorParameters(JoinPoint joinPoint) {
        Constructor<?> constructor = ConstructorSignature.class.cast(joinPoint.getSignature()).getConstructor();
        Object[] parameterValues = joinPoint.getArgs();
        Set<ConstraintViolation<Object>> theViolations = Validation.buildDefaultValidatorFactory().getValidator()
                .forExecutables().validateConstructorParameters(constructor, parameterValues);
        if (theViolations.size() > 0)
            throw new ConstraintViolationException(theViolations);
    }

    @Before("execution(* fr.pinguet62.test.jsr303..*.*(..))"
            // fix
            + " && !within(fr.pinguet62.test.jsr303.Jsr303ValidationAspect)")
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

    @AfterReturning(value = "execution(* fr.pinguet62.test.jsr303..*.*(..))"
            // fix
            + " && !within(fr.pinguet62.test.jsr303.Jsr303ValidationAspect)", returning = "returnValue")
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

}