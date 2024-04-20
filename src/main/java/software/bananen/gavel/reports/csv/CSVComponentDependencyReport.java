package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.ComponentDependency;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A CSV based report for the component dependency metrics.
 */
public class CSVComponentDependencyReport
        implements Report<Collection<ComponentDependency>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVComponentDependencyReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVComponentDependencyReport(final File targetDirectory) {
        this.targetDirectory = requireNonNull(targetDirectory, "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final Collection<ComponentDependency> measurements) throws ReportException {
        try {
            final File targetFile = CSVWriter.getFileIn(
                    targetDirectory,
                    "component-dependency-metrics.csv");

            CSVWriter.write(targetFile,
                    List.of("package", "Ce", "Ca", "I", "A", "D"),
                    List.of(ComponentDependency::packageName,
                            ComponentDependency::efferentCoupling,
                            ComponentDependency::afferentCoupling,
                            ComponentDependency::instability,
                            ComponentDependency::abstractness,
                            ComponentDependency::normalizedDistanceFromMainSequence),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException(
                    "Failed to write component dependency report", e);
        }

    }
}
