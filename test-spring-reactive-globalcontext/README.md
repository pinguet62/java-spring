# Reactor: Global context

### `ThreadLocal`

Problem: A `Thread` can be used to process several asynchronous sequences.

See: https://projectreactor.io/docs/core/3.1.2.RELEASE/reference/#context

### `@Scope(SCOPE_REQUEST)`

Implementation:
```java
@Component
@Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
public static class CurrentUser {
    private String value;
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
```

Usage:
```java
@GetMapping("/a")
public Flux<Object> a() {
    return flux
            .doOnNext(it -> currentUser.setValue("A"))
            // ...
}
```

Error:
```
java.lang.IllegalStateException: No thread-bound request found: Are you referring to request attributes outside of an actual web request, or processing a request outside of the originally receiving thread? If you are actually operating within a web request and still receive this message, your code is probably running outside of DispatcherServlet/DispatcherPortlet: In this case, use RequestContextListener or RequestContextFilter to expose the current request.
	at org.springframework.web.context.request.RequestContextHolder.currentRequestAttributes(RequestContextHolder.java:131) ~[spring-web-5.0.8.RELEASE.jar:5.0.8.RELEASE]
	at org.springframework.web.context.request.AbstractRequestAttributesScope.get(AbstractRequestAttributesScope.java:42) ~[spring-web-5.0.8.RELEASE.jar:5.0.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:350) ~[spring-beans-5.0.8.RELEASE.jar:5.0.8.RELEASE]
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) ~[spring-beans-5.0.8.RELEASE.jar:5.0.8.RELEASE]
	at org.springframework.aop.target.SimpleBeanTargetSource.getTarget(SimpleBeanTargetSource.java:35) ~[spring-aop-5.0.8.RELEASE.jar:5.0.8.RELEASE]
	at org.springframework.aop.framework.CglibAopProxy$DynamicAdvisedInterceptor.intercept(CglibAopProxy.java:672) ~[spring-aop-5.0.8.RELEASE.jar:5.0.8.RELEASE]
	at fr.pinguet62.test.springreactiveglobalcontext.beanscoperequest.TestBeanRequestScope$CurrentUser$$EnhancerBySpringCGLIB$$7811a8ed.getValue(<generated>) ~[classes/:na]
	at fr.pinguet62.test.springreactiveglobalcontext.beanscoperequest.TestBeanRequestScope.printTest(TestBeanRequestScope.java:58) ~[classes/:na]
	at fr.pinguet62.test.springreactiveglobalcontext.beanscoperequest.TestBeanRequestScope.lambda$a$1(TestBeanRequestScope.java:41) ~[classes/:na]
	at reactor.core.publisher.FluxPeek$PeekSubscriber.onNext(FluxPeek.java:177) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxConcatMap$ConcatMapImmediate.innerNext(FluxConcatMap.java:271) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.FluxConcatMap$ConcatMapInner.onNext(FluxConcatMap.java:803) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.Operators$MonoSubscriber.complete(Operators.java:1083) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoDelayUntil$DelayUntilCoordinator.signal(MonoDelayUntil.java:211) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoDelayUntil$DelayUntilTrigger.onComplete(MonoDelayUntil.java:290) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.publisher.MonoDelay$MonoDelayRunnable.run(MonoDelay.java:118) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.scheduler.SchedulerTask.call(SchedulerTask.java:50) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at reactor.core.scheduler.SchedulerTask.call(SchedulerTask.java:27) ~[reactor-core-3.1.8.RELEASE.jar:3.1.8.RELEASE]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[na:1.8.0_161]
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:180) ~[na:1.8.0_161]
	at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:293) ~[na:1.8.0_161]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[na:1.8.0_161]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[na:1.8.0_161]
	at java.lang.Thread.run(Thread.java:748) [na:1.8.0_161]
```
