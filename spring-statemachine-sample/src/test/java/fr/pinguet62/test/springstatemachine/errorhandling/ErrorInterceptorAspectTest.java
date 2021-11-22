package fr.pinguet62.test.springstatemachine.errorhandling;

import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspect.InterceptInternalStateMachineErrors;
import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspectTest.TestActions;
import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspectTest.TestIndirectServices;
import fr.pinguet62.test.springstatemachine.errorhandling.ErrorInterceptorAspectTest.TestServices;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;


@SpringBootTest(classes = {ErrorInterceptorAspect.class, TestServices.class, TestIndirectServices.class, TestActions.class})
class ErrorInterceptorAspectTest {

    // Separated class for proxy
    @Component
    static class TestActions {
        void successfullAction(StateContext<String, String> stateContext) {
        }

        void anyFallingAction(StateContext<String, String> stateContext) {
            throw new RuntimeException("anyFallingAction");
        }

        void firstFallingAction(StateContext<String, String> stateContext) {
            throw new RuntimeException("firstFallingAction");
        }

        void secondFallingAction(StateContext<String, String> stateContext) {
            throw new RuntimeException("secondFallingAction");
        }
    }

    @Component
    static class TestServices {
        @Autowired
        TestActions actions;

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
    static class TestIndirectServices {
        @Autowired
        TestServices subService;

        @Autowired
        TestActions actions;

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
    TestServices service;

    @Autowired
    TestIndirectServices indirectService;

    @Test
    void test_onlyFirstIsRethrown() {
        try {
            service.executeFallingActions();
            fail("Aspect should throw an Exception");
        } catch (InternalStateMachineException e) {
            assertEquals(e.getCause().getMessage(), "firstFallingAction");
            assertFalse(e.getNextThrowable().isEmpty()); // because 2 errors
        }
    }

    @Test
    void test_subInterceptions() {
        try {
            indirectService.executeIndirect();
            fail("Aspect should throw an Exception");
        } catch (InternalStateMachineException e) {
            assertEquals(e.getCause().getMessage(), "anyFallingAction");
            assertFalse(e.getNextThrowable().isEmpty()); // because 2 errors
        }
    }
}
