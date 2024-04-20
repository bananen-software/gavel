package software.bananen.gavel.reports;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A helper that eases the creation of performing a measurement and then
 * reporting based on the measurements.
 *
 * @param <T> The type of the measurement.
 */
public class ReportChain<T> {

    private final Supplier<T> measurement;

    /**
     * Creates a new instance.
     *
     * @param measurement The measurement supplier.
     */
    private ReportChain(final Supplier<T> measurement) {
        this.measurement =
                requireNonNull(measurement, "The measurement may not be null");
    }

    /**
     * Reports the measurements based on the given report.
     *
     * @param report The report.
     * @throws ReportException Might be thrown in case that reporting
     *                                    the measurements failed.
     */
    public void andReport(final Report<T> report) throws ReportException {
        report.report(measurement.get());
    }

    public static <T> ReportChain<T> measure(final Supplier<T> measurement) {
        return new ReportChain<>(measurement);
    }
}
