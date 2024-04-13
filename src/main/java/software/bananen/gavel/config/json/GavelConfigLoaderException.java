package software.bananen.gavel.config.json;

/**
 * An exception that indicates that the gavel config could not be loaded.
 */
public class GavelConfigLoaderException extends Exception {

    /**
     * Creates a new instance.
     *
     * @param message The message.
     * @param cause   The actual cause.
     */
    public GavelConfigLoaderException(String message,
                                      Exception cause) {
        super(message, cause);
    }
}
