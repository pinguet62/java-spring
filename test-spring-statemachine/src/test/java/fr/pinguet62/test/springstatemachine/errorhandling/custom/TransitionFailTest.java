package fr.pinguet62.test.springstatemachine.errorhandling.custom;

import fr.pinguet62.test.springstatemachine.errorhandling.InternalStateMachineException;
import fr.pinguet62.test.springstatemachine.errorhandling.MyEvent;
import fr.pinguet62.test.springstatemachine.errorhandling.MyState;
import fr.pinguet62.test.springstatemachine.errorhandling.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;

import static java.util.EnumSet.allOf;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.statemachine.config.StateMachineBuilder.builder;

@SpringBootTest(classes = TestConfig.class)
class TransitionFailTest {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    Action<MyState, MyEvent> fallingAction;

    @Autowired
    MyService service;

    StateMachine<MyState, MyEvent> build() throws Exception {
        Builder<MyState, MyEvent> builder = builder();
        // @formatter:off
        builder.configureConfiguration()
            .withConfiguration()
                .autoStartup(true)
                .beanFactory(beanFactory);
        builder.configureStates()
            .withStates()
                .states(allOf(MyState.class))
                .initial(MyState.A);
        builder.configureTransitions()
            .withExternal()
                .event(MyEvent.Sample)
                .source(MyState.A)
                .action(fallingAction) // test
                .target(MyState.B);
        // @formatter:on
        return builder.build();
    }

    @Test
    void test() throws Exception {
        StateMachine<MyState, MyEvent> stateMachine = build();

        try {
            service.sendSampleEvent(stateMachine);
            fail();
        } catch (InternalStateMachineException e) {
            // ok
        }
    }
}
