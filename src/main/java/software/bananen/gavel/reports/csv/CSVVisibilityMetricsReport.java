package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.ComponentVisibility;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A CSV based visibility metrics report.
 */
public class CSVVisibilityMetricsReport
        implements Report<Collection<ComponentVisibility>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVVisibilityMetricsReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVVisibilityMetricsReport(final File targetDirectory) {
        this.targetDirectory = requireNonNull(targetDirectory,
                "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(Collection<ComponentVisibility> measurements) throws ReportException {
        try {
            final File targetFile = CSVWriter.getFileIn(targetDirectory,
                    "visibility-metrics.csv");

            CSVWriter.write(targetFile,
                    List.of("package", "RV", "ARV", "GRV"),
                    List.of(ComponentVisibility::packageName,
                            ComponentVisibility::relativeVisibility,
                            ComponentVisibility::averageRelativeVisibility,
                            ComponentVisibility::globalRelativeVisibility),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException("Failed to write visibility metrics file", e);
        }
    }
}
