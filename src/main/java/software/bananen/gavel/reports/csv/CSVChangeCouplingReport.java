package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.ChangeCouplingMetric;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A CSV based report for the change coupling
 */
public final class CSVChangeCouplingReport
        implements Report<Collection<ChangeCouplingMetric>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVChangeCouplingReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVChangeCouplingReport(final File targetDirectory) {
        this.targetDirectory = requireNonNull(targetDirectory,
                "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final Collection<ChangeCouplingMetric> measurements)
            throws ReportException {
        try {
            final File targetFile = CSVWriter.getFileIn(
                    targetDirectory,
                    "change-coupling.csv");

            //origin, target, total changes, coupled changes, coupling

            CSVWriter.write(targetFile,
                    List.of("origin", "target", "total changes", "coupled changes", "coupling"),
                    List.of(ChangeCouplingMetric::origin,
                            ChangeCouplingMetric::target,
                            ChangeCouplingMetric::totalChanges,
                            ChangeCouplingMetric::coupledChanges,
                            ChangeCouplingMetric::coupling),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException(
                    "Failed to write change coupling report", e);
        }
    }
}
