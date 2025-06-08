package software.bananen.gavel.contextloader;

import java.io.Serial;

/**
 * An exception that is thrown when the project context could not be loaded.
 */
public class ProjectContextLoaderException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     *
     * @param message The message.
     * @param cause   The actual cause.
     */
    public ProjectContextLoaderException(String message,
                                         Exception cause) {
        super(message, cause);
    }
}
