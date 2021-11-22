package fr.pinguet62.test.jackson;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class NillableTest {

    @Test
    void test_equals() {
        assertEquals(Nillable.undefined(), Nillable.undefined());
        assertNotEquals(Nillable.undefined(), Nillable.ofDefined("any"));
        assertNotEquals(Nillable.ofDefined("first"), Nillable.ofDefined("second"));
        assertEquals(Nillable.ofDefined("any"), Nillable.ofDefined("any"));
    }

    @Test
    void test_hashCode() {
        assertThat(Nillable.ofDefined("value").hashCode(), is(Objects.hash(true, "value")));
    }

}
