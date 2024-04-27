package fr.pinguet62.sax;

import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.Reader;

/**
 * Simple {@link LSInput} POJO.
 */
@Getter
@Setter
public class LSInputImpl implements LSInput {

    private String baseURI;

    private InputStream byteStream;

    private Boolean certifiedText;

    private Reader characterStream;

    private String encoding;

    private String publicId;

    private String stringData;

    private String systemId;

    @Override
    public boolean getCertifiedText() {
        return certifiedText;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        this.certifiedText = certifiedText;
    }
}
