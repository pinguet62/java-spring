package fr.pinguet62.test.springstatemachine.errorhandling.custom;

import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;

import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspect.InterceptInternalStateMachineErrors;
import fr.pinguet62.test.springstatemachine.errorhandling.MyEvent;
import fr.pinguet62.test.springstatemachine.errorhandling.MyState;

@Service
public class MyService {

    @InterceptInternalStateMachineErrors
    public boolean sendSampleEvent(StateMachine<MyState, MyEvent> stateMachine) {
        return stateMachine.sendEvent(MyEvent.Sample);
    }

}