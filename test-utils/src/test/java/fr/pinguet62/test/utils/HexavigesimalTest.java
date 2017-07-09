package fr.pinguet62.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

public final class HexavigesimalTest {

    /**
     * Test for {@link fr.pinguet62.utils.hexavigesimal.Hexavigesimal#parse(String)}. <br />
     * Validate the correct conversion.
     */
    @Test
    public void test_parse() {
        assertEquals(0, Hexavigesimal.parse("A"));
        assertEquals(1, Hexavigesimal.parse("B"));
        assertEquals(24, Hexavigesimal.parse("Y"));
        assertEquals(25, Hexavigesimal.parse("Z"));
        assertEquals(26, Hexavigesimal.parse("AA"));
        assertEquals(27, Hexavigesimal.parse("AB"));
        assertEquals(51, Hexavigesimal.parse("AZ"));
        assertEquals(52, Hexavigesimal.parse("BA"));
        assertEquals(53, Hexavigesimal.parse("BB"));
        assertEquals(700, Hexavigesimal.parse("ZY"));
        assertEquals(701, Hexavigesimal.parse("ZZ"));
        assertEquals(702, Hexavigesimal.parse("AAA"));
        assertEquals(703, Hexavigesimal.parse("AAB"));
    }

    /**
     * Test for {@link fr.pinguet62.utils.hexavigesimal.Hexavigesimal#parse(String)}. <br />
     * Validate the correct argument control of argument.
     */
    @Test
    public void test_parse_invalid() {
        for (String representation : Arrays.asList(null, "", "123", "abc", "A-Z"))
            try {
                Hexavigesimal.parse(representation);
                fail();
            } catch (IllegalArgumentException e) {
            }
    }

    /**
     * Test for {@link fr.pinguet62.utils.hexavigesimal.Hexavigesimal#toString()} . <br />
     * Validate the correct format of {@link String} representation.
     */
    @Test
    public void test_toString() {
        assertEquals("A", new Hexavigesimal(0).toString());
        assertEquals("B", new Hexavigesimal(1).toString());
        assertEquals("Y", new Hexavigesimal(24).toString());
        assertEquals("Z", new Hexavigesimal(25).toString());
        assertEquals("AA", new Hexavigesimal(26).toString());
        assertEquals("AB", new Hexavigesimal(27).toString());
        assertEquals("AZ", new Hexavigesimal(51).toString());
        assertEquals("BA", new Hexavigesimal(52).toString());
        assertEquals("BB", new Hexavigesimal(53).toString());
        assertEquals("ZY", new Hexavigesimal(700).toString());
        assertEquals("ZZ", new Hexavigesimal(701).toString());
        assertEquals("AAA", new Hexavigesimal(702).toString());
        assertEquals("AAB", new Hexavigesimal(703).toString());
    }

}