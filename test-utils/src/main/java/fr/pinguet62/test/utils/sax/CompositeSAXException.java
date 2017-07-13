package fr.pinguet62.test.utils.sax;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

public class CompositeSAXException extends SAXException {

    private static final long serialVersionUID = 1;

    private List<SAXException> exceptions = new ArrayList<>();

    /**
     * Append each {@link SAXException#getMessage()} to single {@link String}, separated by
     * {@link System#lineSeparator()}.
     */
    @Override
    public String getMessage() {
        // AbstractCollection::toString() use "[foo, bar]" format
        // TODO Java 8
        AbstractCollection<String> message = new ArrayList<>();
        for (SAXException exception : exceptions)
            message.add(exception.getMessage());
        return message.toString();
    }

    public List<SAXException> getExceptions() {
        return exceptions;
    }

}