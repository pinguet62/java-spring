package fr.pinguet62.test.utils;

import fr.pinguet62.test.utils.sax.CompositeErrorHandler;
import fr.pinguet62.test.utils.sax.LSResourceResolverImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Path;

import static java.nio.file.Files.newInputStream;

public class XmlUtils {

    /**
     * @throws SAXException {@link Validator#validate(Source)} (wrapping {@link IOException})
     */
    public static void validate(Source xml, Path xsdPath) throws SAXException {
        Schema schema = schema(xsdPath);
        Validator validator = schema.newValidator();
        CompositeErrorHandler errorHandler = new CompositeErrorHandler();
        validator.setErrorHandler(errorHandler);

        try {
            validator.validate(xml);
        } catch (IOException e) {
            // to use only 1 Exception
            throw new SAXException(e);
        }

        errorHandler.throwIfErrors();
    }

    /**
     * Convert {@link Path} to {@link Schema}.
     */
    public static Schema schema(Path xsd) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new LSResourceResolverImpl(xsd));
            return factory.newSchema(new StreamSource(newInputStream(xsd)));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Convert {@link String} to {@link Source}.
     *
     * @see InputSource#InputSource(Reader)
     */
    public static Source source(String xml) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            InputSource inputSource = new InputSource(new StringReader(xml));
            Document document = documentBuilderFactory.newDocumentBuilder().parse(inputSource);
            return new DOMSource(document);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
