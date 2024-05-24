package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.AuthorComplexityHistoryEntry;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class CSVAuthorComplexityReport
        implements Report<Collection<AuthorComplexityHistoryEntry>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVAuthorComplexityReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory that the report should be
     *                        written to.
     */
    public CSVAuthorComplexityReport(final File targetDirectory) {
        this.targetDirectory =
                requireNonNull(targetDirectory, "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final Collection<AuthorComplexityHistoryEntry> measurements) throws ReportException {
        try {
            final File targetFile = CSVWriter.getFileIn(
                    targetDirectory,
                    "author-complexity.csv");

            CSVWriter.write(targetFile,
                    List.of("author", "complexity delta", "number of changes", "relative complexity added"),
                    List.of((e) -> e.author().name() + " <" + e.author().email() + ">",
                            AuthorComplexityHistoryEntry::complexityDelta,
                            AuthorComplexityHistoryEntry::numberOfChanges,
                            AuthorComplexityHistoryEntry::relativeComplexityAdded),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException(
                    "Failed to write author complexity report", e);
        }
    }
}
