package fr.pinguet62;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.Source;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static fr.pinguet62.XmlUtils.source;
import static fr.pinguet62.XmlUtils.validate;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlUtilsTest {

    @Test
    void test_isValid_valid() throws Exception {
        Source xml = getXml("/xml/sample-valid.xml");
        Path xsd = getSubResource("/xsd/root.xsd");

        validate(xml, xsd);
    }

    @Test
    void test_isValid_invalidXsd() throws Exception {
        Source xml = getXml("/xml/sample-valid.xml");
        Path xsd = getSubResource("/xsd/invalid.xsd");

        assertThrows(IllegalArgumentException.class, () -> validate(xml, xsd));
    }

    @Test
    void test_isValid_invalidXml() throws Exception {
        Source xml = getXml("/xml/sample-invalid.xml");
        Path xsd = getSubResource("/xsd/root.xsd");

        assertThrows(SAXException.class, () -> validate(xml, xsd));
    }

    /**
     * @param Sub-path of resource. Must start by {@code "/"}.
     */
    private Path getSubResource(String subResourcePath) throws Exception {
        URL resourcePath = getClass().getResource("/" + getClass().getName().replace('.', '/') + subResourcePath);
        return Paths.get(resourcePath.toURI());
    }

    /**
     * @param Sub-path of resource. Must start by {@code "/"}.
     */
    private Source getXml(String subResourcePath) throws Exception {
        return source(new String(Files.readAllBytes(getSubResource(subResourcePath))));
    }
}
