package gavel.staticanalysis.adapter.owaspdependencycheck;

/**
 * An exception that can be thrown by analysis adapters.
 */
public class AnalysisAdapterException extends Exception {

    /**
     * Creates a new instance.
     *
     * @param message The message that describes the exception.
     * @param cause   The actual cause of the exception.
     */
    public AnalysisAdapterException(final String message,
                                    final Throwable cause) {
        super(message, cause);
    }
}
