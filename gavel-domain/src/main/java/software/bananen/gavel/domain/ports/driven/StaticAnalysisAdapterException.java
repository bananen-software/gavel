package software.bananen.gavel.domain.ports.driven;

import java.io.Serial;

/**
 * An exception that can be thrown by analysis adapters.
 */
public class StaticAnalysisAdapterException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     *
     * @param message The message that describes the exception.
     * @param cause   The actual cause of the exception.
     */
    public StaticAnalysisAdapterException(final String message,
                                          final Throwable cause) {
        super(message, cause);
    }
}
