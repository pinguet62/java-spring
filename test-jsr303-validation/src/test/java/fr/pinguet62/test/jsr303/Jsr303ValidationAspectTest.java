package fr.pinguet62.test.jsr303;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.junit.Test;

public class Jsr303ValidationAspectTest {

    public static class Service {
        private final List<String> result;

        /** Constructor to mock {@link #getEmail(String)}. */
        public Service(List<String> result) {
            this.result = result;
        }

        @NotNull
        @Size(min = 1)
        public List<@Email String> getEmail(@NotNull @Length(min = 2, max = 5) String user) {
            return result;
        }
    }

    public static class ConstructorClass {
        public ConstructorClass(@Length(min = 2, max = 5) String arg) {
        }
    }

    @Test
    public void test_return() {
        Service serice = new Service(asList("foo@bar.org"));
        serice.getEmail("any");
    }

    @Test // ok
    public void test_constructor() {
        new ConstructorClass("any");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test_constructor_Length() {
        new ConstructorClass("");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test_return_NotNull() {
        Service serice = new Service(null);
        serice.getEmail("any");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test_return_Size() {
        Service serice = new Service(new ArrayList<>());
        serice.getEmail("any");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test_return_generic_Email() {
        Service serice = new Service(asList("invalid"));
        serice.getEmail("any");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test_parameter_NotNull() {
        Service serice = new Service(asList("foo@bar.org"));
        serice.getEmail(null);
    }

    @Test(expected = ConstraintViolationException.class)
    public void test_parameter_Length_min() {
        Service serice = new Service(asList("foo@bar.org"));
        serice.getEmail("");
    }

    @Test(expected = ConstraintViolationException.class)
    public void test_parameter_Length_max() {
        Service serice = new Service(asList("foo@bar.org"));
        serice.getEmail("xxxxxxxxxxxxxxxxxxxxxxxxxxx");
    }

}