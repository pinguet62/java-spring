package fr.pinguet62.test.utils.sax;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Paths.get;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * Implementation of {@link LSResourceResolver} who use only <b>relative path</b> to find included and imported other
 * XSD files. Use absolute {@link Path} to access to files.
 */
public class LSResourceResolverImpl implements LSResourceResolver {

    private final Path rootXsdFolderPath;

    public LSResourceResolverImpl(Path schemaBasePath) {
        this.rootXsdFolderPath = schemaBasePath.getParent();
    }

    /**
     * @param baseURI {@link URI} of XSD in process.
     * @param systemId Relative path (starting or not with {@code "./"} or {@code "../"}) of target included/imported
     *        XSD. See {@code schemaLocation} tag attribute.
     */
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        Path currentXsdFolderPath = baseURI == null ? rootXsdFolderPath : get(URI.create(baseURI)).getParent();
        Path systemIdPath = currentXsdFolderPath.resolve(systemId).normalize();

        InputStream inputStream;
        try {
            inputStream = newInputStream(systemIdPath);
        } catch (IOException e) {
            // TODO Java 8: UncheckedIOException
            throw new RuntimeException(e);
        }

        LSInput lsInput = new LSInputImpl();
        lsInput.setByteStream(inputStream);
        lsInput.setPublicId(publicId);
        lsInput.setSystemId(systemIdPath.toUri().toString());
        return lsInput;
    }

}