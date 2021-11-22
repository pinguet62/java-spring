package fr.pinguet62.test.springdatathrowifnotfound.config;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.data.util.AnnotationDetectionMethodCallback;
import org.springframework.util.ReflectionUtils;

public class ThrowIfNotFoundRepositoryProxyPostProcessor implements RepositoryProxyPostProcessor {

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        if (hasAtLeastOneAnnotation(repositoryInformation)) {
            factory.addAdvice(new ThrowIfNotFoundMethodInterceptor());
        }
    }

    private boolean hasAtLeastOneAnnotation(RepositoryInformation repositoryInformation) {
        AnnotationDetectionMethodCallback<ThrowIfNotFound> methodCallback = new AnnotationDetectionMethodCallback<>(ThrowIfNotFound.class, false);
        ReflectionUtils.doWithMethods(repositoryInformation.getRepositoryInterface(), methodCallback);
        return methodCallback.hasFoundAnnotation();
    }
}
