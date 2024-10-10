package software.bananen.gavel.contextloader;

/**
 * An exception that is thrown when the project context could not be loaded.
 */
public class ProjectContextLoaderException extends Exception {

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
