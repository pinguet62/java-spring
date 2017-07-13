package fr.pinguet62.test.utils;

import static fr.pinguet62.test.utils.XmlUtils.source;
import static fr.pinguet62.test.utils.XmlUtils.validate;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.Source;

import org.junit.Test;
import org.xml.sax.SAXException;

/** @see XmlUtils */
public class XmlUtilsTest {

    @Test
    public void test_isValid_valid() throws Exception {
        Source xml = getXml("/xml/sample-valid.xml");
        Path xsd = getSubResource("/xsd/root.xsd");

        validate(xml, xsd);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_isValid_invalidXsd() throws Exception {
        Source xml = getXml("/xml/sample-valid.xml");
        Path xsd = getSubResource("/xsd/invalid.xsd");

        validate(xml, xsd); // IllegalArgumentException
    }

    @Test(expected = SAXException.class)
    public void test_isValid_invalidXml() throws Exception {
        Source xml = getXml("/xml/sample-invalid.xml");
        Path xsd = getSubResource("/xsd/root.xsd");

        validate(xml, xsd); // SAXException
    }

    /** @param Sub-path of resource. Must start by {@code "/"}. */
    private Path getSubResource(String subResourcePath) throws Exception {
        URL resourcePath = getClass().getResource("/" + getClass().getName().replace('.', '/') + subResourcePath);
        return Paths.get(resourcePath.toURI());
    }

    /** @param Sub-path of resource. Must start by {@code "/"}. */
    private Source getXml(String subResourcePath) throws Exception {
        return source(new String(Files.readAllBytes(getSubResource(subResourcePath))));
    }

}