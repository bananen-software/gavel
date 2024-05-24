package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.CodeHotspot;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class CSVCodeHotspotReport
        implements Report<Collection<CodeHotspot>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVCodeHotspotReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVCodeHotspotReport(final File targetDirectory) {
        this.targetDirectory = requireNonNull(targetDirectory, "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final Collection<CodeHotspot> measurements) throws ReportException {
        try {
            final File targetFile = CSVWriter.getFileIn(
                    targetDirectory,
                    "code-hotspots.csv");

            CSVWriter.write(targetFile,
                    List.of("file", "number of changes"),
                    List.of(CodeHotspot::fileName,
                            CodeHotspot::numberOfChanges),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException(
                    "Failed to write code hotspot report", e);
        }
    }
}
