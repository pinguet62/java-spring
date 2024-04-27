package fr.pinguet62.sax;

import lombok.Getter;
import org.xml.sax.SAXException;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.List;

public class CompositeSAXException extends SAXException {

    private static final long serialVersionUID = 1;

    @Getter
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
}
