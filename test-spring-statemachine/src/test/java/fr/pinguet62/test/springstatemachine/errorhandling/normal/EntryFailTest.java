package fr.pinguet62.test.springstatemachine.errorhandling.normal;

import static java.util.EnumSet.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

import fr.pinguet62.test.springstatemachine.errorhandling.MyEvent;
import fr.pinguet62.test.springstatemachine.errorhandling.MyState;
import fr.pinguet62.test.springstatemachine.errorhandling.TestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class EntryFailTest {

    @Autowired
    private BeanFactory beanFactory;

    @Autowired
    private Action<MyState, MyEvent> fallingAction;

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
                .initial(MyState.A)
                .stateEntry(MyState.B, fallingAction); // test
        builder.configureTransitions()
            .withExternal()
                .source(MyState.A)
                .event(MyEvent.Sample)
                .target(MyState.B);
        // @formatter:on
        return builder.build();
    }

    @Test
    public void test() throws Exception {
        StateMachine<MyState, MyEvent> stateMachine = build();

        boolean result = stateMachine.sendEvent(MyEvent.Sample);

        assertTrue(result); // event processed
        assertEquals(MyState.B, stateMachine.getState().getId()); // keep target state
    }

}