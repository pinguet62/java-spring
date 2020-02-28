package fr.pinguet62.test.jackson;

import org.junit.Test;

import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

public class NillableTest {

    @Test
    public void test_equals() {
        assertEquals(Nillable.undefined(), Nillable.undefined());
        assertNotEquals(Nillable.undefined(), Nillable.ofDefined("any"));
        assertNotEquals(Nillable.ofDefined("first"), Nillable.ofDefined("second"));
        assertEquals(Nillable.ofDefined("any"), Nillable.ofDefined("any"));
    }

    @Test
    public void test_hashCode() {
        assertThat(Nillable.ofDefined("value").hashCode(), is(Objects.hash(true, "value")));
    }

}
