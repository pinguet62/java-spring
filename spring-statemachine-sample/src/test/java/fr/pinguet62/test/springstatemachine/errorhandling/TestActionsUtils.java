package fr.pinguet62.test.springstatemachine.errorhandling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

@Configuration
public class TestActionsUtils {

    @Bean // proxied if necessary
    public Action<MyState, MyEvent> fallingAction() {
        return new Action<MyState, MyEvent>() {
            @Override
            public void execute(StateContext<MyState, MyEvent> c) {
                throw new RuntimeException("test error");
            }
        };
    }
}
