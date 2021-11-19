package fr.pinguet62.test.utils.sax;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CompositeErrorHandler implements ErrorHandler {

    private final CompositeSAXException errors = new CompositeSAXException();

    @Override
    public void error(SAXParseException exception) throws SAXException {
        processError(exception);
    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {
        processError(exception);
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {
        // not important
    }

    private void processError(SAXParseException exception) {
        errors.getExceptions().add(exception);
    }

    /**
     * @throws SAXException If any error occurred during process.
     */
    public void throwIfErrors() throws SAXException {
        if (!errors.getExceptions().isEmpty())
            throw errors;
    }
}
