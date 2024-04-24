package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.RelationalCohesion;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A CSV based report for the relational cohesion metrics.
 */
public final class CSVRelationalCohesionReport implements Report<Collection<RelationalCohesion>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVRelationalCohesionReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVRelationalCohesionReport(final File targetDirectory) {
        this.targetDirectory = requireNonNull(targetDirectory,
                "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final Collection<RelationalCohesion> measurements) throws ReportException {
        try {
            final File targetFile = CSVWriter.getFileIn(targetDirectory,
                    "relational-cohesion.csv");

            CSVWriter.write(targetFile,
                    List.of("package",
                            "Number of types",
                            "Number of internal relationships",
                            "Relational cohesion"),
                    List.of(RelationalCohesion::packageName,
                            RelationalCohesion::numberOfTypes,
                            RelationalCohesion::numberOfInternalRelationships,
                            RelationalCohesion::relationalCohesion),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException("Failed to write visibility metrics file", e);
        }
    }
}
