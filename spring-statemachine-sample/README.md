# Test

## Spring StateMachine: Error handling

### Default behaviour

When an error occurs into `Action`, no `Exception` is thrown.

### Workaround

Intercept all errors (by AOP), and re-throw errors at the end.

![ERROR](./doc/general.png?raw=true)

### Usage

```java
@InterceptInternalStateMachineErrors
public void doSomething() throws InternalStateMachineException {
	StateMachine<MyState, MyEvent> stateMachine;
	stateMachine.sendEvent(...);
}
```

### Requirements

* Actions should be **proxied** (this version uses Spring AspectJ support)

### Limitation

* Not *rollback* `Action` (Example: if previous created a file, the file keep alive)
* Not *interupt* action chain, if the event trigger several `Action`s (Example: if an event trigger 2 `Action`s, and the 1st fails, the second will still be
  executed)
