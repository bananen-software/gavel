package software.bananen.gavel.domain.ports.driven;

/**
 * A port that can be implemented to detect mime-types.
 */
public interface MimeTypeDetectionPort {

    /**
     * Attempts to detect the mime type based on the given filename.
     *
     * @param fileName The filename.
     * @return The detected mime-type.
     */
    String detect(String fileName);
}
