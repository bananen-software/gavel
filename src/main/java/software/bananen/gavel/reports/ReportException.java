package software.bananen.gavel.reports;

/**
 * An exception that can be thrown in case that a reporting measurements failed.
 */
public class ReportException extends Exception {

    /**
     * Creates a new instance.
     *
     * @param message The message.
     * @param cause   The actual cause.
     */
    public ReportException(String message, Exception cause) {
        super(message, cause);
    }
}
