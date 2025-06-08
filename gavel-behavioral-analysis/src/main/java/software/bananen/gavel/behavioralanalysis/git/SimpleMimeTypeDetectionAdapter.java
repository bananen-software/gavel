package software.bananen.gavel.behavioralanalysis.git;

import software.bananen.gavel.domain.ports.driven.MimeTypeDetectionPort;

import java.net.URLConnection;

/**
 * A simple adapter that enables mime type detection.
 */
public class SimpleMimeTypeDetectionAdapter implements MimeTypeDetectionPort {

    /**
     * {@inheritDoc}
     */
    @Override
    public String detect(final String fileName) {
        return URLConnection.guessContentTypeFromName(fileName);
    }
}
