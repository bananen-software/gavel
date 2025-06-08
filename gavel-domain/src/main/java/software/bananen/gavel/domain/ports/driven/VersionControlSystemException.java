package software.bananen.gavel.domain.ports.driven;

import java.io.Serial;

/**
 * An exception that indicates that the interactions with the version control system failed.
 */
public class VersionControlSystemException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     *
     * @param message The message.
     */
    public VersionControlSystemException(final String message) {
        super(message);
    }

    /**
     * Creates a new instance.
     *
     * @param message The message.
     * @param cause   The cause.
     */
    public VersionControlSystemException(final String message,
                                         final Throwable cause) {
        super(message, cause);
    }
}
