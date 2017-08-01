package fr.pinguet62.test.springstatemachine.errorhandling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;

@Configuration
public class TestActionsUtils {

    @Bean // proxied if necessary
    public Action<MyState, MyEvent> fallingAction() {
        return c -> {
            throw new RuntimeException("test error");
        };
    }

}