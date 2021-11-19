package fr.pinguet62.test.springdatathrowifnotfound.config;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

public class ThrowIfNotFoundMethodInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object result = invocation.proceed();
        if (result == null) {
            ThrowIfNotFound resourceNotFound = method.getAnnotation(ThrowIfNotFound.class);
            if (resourceNotFound != null) {
                throw new NotFoundException();
            }
        }
        return result;
    }
}
