package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.CumulativeComponentDependency;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A CSV based report for the cumulative component dependencies.
 */
public class CSVCumulativeComponentDependencyReport
        implements Report<Collection<CumulativeComponentDependency>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVCumulativeComponentDependencyReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVCumulativeComponentDependencyReport(final File targetDirectory) {
        this.targetDirectory = requireNonNull(targetDirectory,
                "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final Collection<CumulativeComponentDependency> measurements)
            throws ReportException {
        try {
            File targetFile =
                    CSVWriter.getFileIn(targetDirectory,
                            "cumulative-dependency-metrics.csv");

            CSVWriter.write(targetFile,
                    List.of("package", "CCD", "ACD", "RACD", "NCCD"),
                    List.of(CumulativeComponentDependency::packageName,
                            CumulativeComponentDependency::cumulative,
                            CumulativeComponentDependency::average,
                            CumulativeComponentDependency::relativeAverage,
                            CumulativeComponentDependency::normalized),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException("Failed to write cumulative dependency metrics to file", e);
        }
    }
}
