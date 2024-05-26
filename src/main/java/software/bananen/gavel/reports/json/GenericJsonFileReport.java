package software.bananen.gavel.reports.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * A generic {@link Report} that writes measurements to a JSON file.
 *
 * @param <T> The type of the reported measurements.
 */
public final class GenericJsonFileReport<T> implements Report<T> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GenericJsonFileReport.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final File targetDirectory;
    private final String fileName;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory that the report should be
     *                        written to.
     */
    public GenericJsonFileReport(final File targetDirectory,
                                 final String fileName) {
        this.targetDirectory =
                requireNonNull(targetDirectory, "The target directory may not be null");
        this.fileName =
                requireNonNull(fileName, "The file name may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final T measurements) throws ReportException {
        try {
            final File reportFile =
                    targetDirectory.toPath().resolve(fileName).toFile();

            MAPPER.writeValue(reportFile, measurements);

            LOGGER.info("Written JSON report {}", reportFile);
        } catch (final IOException e) {
            throw new ReportException("Failed to write report to JSON file", e);
        }
    }
}
