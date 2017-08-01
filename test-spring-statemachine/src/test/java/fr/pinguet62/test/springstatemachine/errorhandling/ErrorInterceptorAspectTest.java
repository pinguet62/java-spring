package fr.pinguet62.test.springstatemachine.errorhandling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspect.InterceptInternalStateMachineErrors;
import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspectTest.TestActions;
import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspectTest.TestIndirectServices;
import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspectTest.TestServices;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { ErrorInterceptorAspect.class, TestServices.class, TestIndirectServices.class, TestActions.class })
public class ErrorInterceptorAspectTest {

    // Separated class for proxy
    @Component
    public static class TestActions {
        public void successfullAction(StateContext<String, String> stateContext) {
        }

        public void anyFallingAction(StateContext<String, String> stateContext) {
            throw new RuntimeException("anyFallingAction");
        }

        public void firstFallingAction(StateContext<String, String> stateContext) {
            throw new RuntimeException("firstFallingAction");
        }

        public void secondFallingAction(StateContext<String, String> stateContext) {
            throw new RuntimeException("secondFallingAction");
        }
    }

    @Component
    public static class TestServices {
        @Autowired
        private TestActions actions;

        @InterceptInternalStateMachineErrors
        public void executeFallingActions() {
            try {
                actions.successfullAction(null);

                try {
                    actions.firstFallingAction(null);
                } catch (Exception e) {
                    // "secure" execution
                }

                try {
                    actions.secondFallingAction(null);
                } catch (Exception e) {
                    // "secure" execution
                }
            } catch (Throwable throwable) {
                // StateMachine catch Exception
                fail("Should never throw error");
            }
        }
    }

    @Component
    public static class TestIndirectServices {
        @Autowired
        private TestServices subService;

        @Autowired
        private TestActions actions;

        @InterceptInternalStateMachineErrors
        public void executeIndirect() {
            try {
                try {
                    actions.anyFallingAction(null);
                } catch (Exception e) {
                    // "secure" execution
                }

                try {
                    subService.executeFallingActions();
                    fail("Aspect should throw an Exception");
                } catch (InternalStateMachineException e) {
                    assertEquals(e.getCause().getMessage(), "firstFallingAction");
                    assertFalse(e.getNextThrowable().isEmpty()); // because 2 errors
                }
            } catch (Throwable throwable) {
                // StateMachine catch Exception
                fail("Should never throw error");
            }
        }
    }

    @Autowired
    private TestServices service;

    @Autowired
    private TestIndirectServices indirectService;

    @Test
    public void test_onlyFirstIsRethrown() {
        try {
            service.executeFallingActions();
            fail("Aspect should throw an Exception");
        } catch (InternalStateMachineException e) {
            assertEquals(e.getCause().getMessage(), "firstFallingAction");
            assertFalse(e.getNextThrowable().isEmpty()); // because 2 errors
        }
    }

    @Test
    public void test_subInterceptions() {
        try {
            indirectService.executeIndirect();
            fail("Aspect should throw an Exception");
        } catch (InternalStateMachineException e) {
            assertEquals(e.getCause().getMessage(), "anyFallingAction");
            assertFalse(e.getNextThrowable().isEmpty()); // because 2 errors
        }
    }

}