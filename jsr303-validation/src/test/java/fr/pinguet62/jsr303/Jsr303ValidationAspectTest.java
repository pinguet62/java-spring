package fr.pinguet62.jsr303;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.external.ExternalModel;
import org.hibernate.validator.constraints.Length;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class Jsr303ValidationAspectTest {

    static class Service {
        final List<String> result;

        /**
         * Constructor to mock {@link #getEmail(String)}.
         */
        public Service(List<String> result) {
            this.result = result;
        }

        @NotNull
        @Size(min = 1)
        public List<@Email String> getEmail(@NotNull @Length(min = 2, max = 5) String user) {
            return result;
        }
    }

    static class ConstructorClass {
        public ConstructorClass(@Length(min = 2, max = 5) String arg) {
        }
    }

    /**
     * Invalid parameter, but not processed.
     *
     * @see Jsr303ValidationAspect#intoApplication()
     */
    @Test
    void test_packageFilter() {
        new ExternalModel().requireNotNull(null);
    }

    @Test
    void test_constructor_ok() {
        new ConstructorClass("any");
    }

    @Test
    void test_constructor_error_Length() {
        assertThrows(ConstraintViolationException.class, () -> new ConstructorClass(""));
    }

    @Test
    void test_parameter_error_NotNull() {
        Service serice = new Service(List.of("foo@bar.org"));
        assertThrows(ConstraintViolationException.class, () -> serice.getEmail(null));
    }

    @Test
    void test_parameter_error_Length_min() {
        Service serice = new Service(List.of("foo@bar.org"));
        assertThrows(ConstraintViolationException.class, () -> serice.getEmail(""));
    }

    @Test
    void test_parameter_error_Length_max() {
        Service serice = new Service(List.of("foo@bar.org"));
        assertThrows(ConstraintViolationException.class, () -> serice.getEmail("xxxxxxxxxxxxxxxxxxxxxxxxxxx"));
    }

    @Test
    void test_return_ok() {
        Service serice = new Service(List.of("foo@bar.org"));
        serice.getEmail("any");
    }

    @Test
    void test_return_error_NotNull() {
        Service serice = new Service(null);
        assertThrows(ConstraintViolationException.class, () -> serice.getEmail("any"));
    }

    @Test
    void test_return_error_Size() {
        Service serice = new Service(new ArrayList<>());
        assertThrows(ConstraintViolationException.class, () -> serice.getEmail("any"));
    }

    @Test
    void test_return_generic_error_Email() {
        Service serice = new Service(List.of("invalid"));
        assertThrows(ConstraintViolationException.class, () -> serice.getEmail("any"));
    }
}
