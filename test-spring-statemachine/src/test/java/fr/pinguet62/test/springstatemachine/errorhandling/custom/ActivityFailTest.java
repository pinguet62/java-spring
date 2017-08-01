package fr.pinguet62.test.springstatemachine.errorhandling.custom;

import static java.util.EnumSet.allOf;
import static org.junit.Assert.fail;
import static org.springframework.statemachine.config.StateMachineBuilder.builder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.StateMachineBuilder.Builder;
import org.springframework.test.context.junit4.SpringRunner;

import fr.pinguet62.test.springstatemachine.errorhandling.InternalStateMachineException;
import fr.pinguet62.test.springstatemachine.errorhandling.MyEvent;
import fr.pinguet62.test.springstatemachine.errorhandling.MyState;
import fr.pinguet62.test.springstatemachine.errorhandling.TestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ActivityFailTest {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private Action<MyState, MyEvent> fallingAction;

    @Autowired
    private MyService service;

    private StateMachine<MyState, MyEvent> build() throws Exception {
        Builder<MyState, MyEvent> builder = builder();
        // @formatter:off
        builder.configureConfiguration()
            .withConfiguration()
                .beanFactory(beanFactory)
                .autoStartup(true);
        builder.configureStates()
            .withStates()
                .states(allOf(MyState.class))
                .initial(MyState.A);
        builder.configureTransitions()
            .withInternal()
                .source(MyState.A)
                .event(MyEvent.Sample)
                .action(fallingAction); // test
        // @formatter:on
        return builder.build();
    }

    @Test
    public void test() throws Exception {
        StateMachine<MyState, MyEvent> stateMachine = build();

        try {
            service.sendSampleEvent(stateMachine);
            fail();
        } catch (InternalStateMachineException e) {
            // ok
        }
    }

}