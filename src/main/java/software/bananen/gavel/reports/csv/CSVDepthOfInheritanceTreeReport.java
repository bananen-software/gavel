package software.bananen.gavel.reports.csv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.metrics.DepthOfInheritanceTree;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A CSV based report for the depth of inheritance tree metrics.
 */
public class CSVDepthOfInheritanceTreeReport
        implements Report<Collection<DepthOfInheritanceTree>> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(CSVDepthOfInheritanceTreeReport.class);

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVDepthOfInheritanceTreeReport(final File targetDirectory) {
        this.targetDirectory = requireNonNull(targetDirectory,
                "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void report(final Collection<DepthOfInheritanceTree> measurements)
            throws ReportException {
        try {
            final File targetFile = CSVWriter.getFileIn(targetDirectory,
                    "depth-of-inheritance-metrics.csv");

            CSVWriter.write(targetFile,
                    List.of("class", "DIT"),
                    List.of(DepthOfInheritanceTree::className,
                            DepthOfInheritanceTree::value),
                    measurements);

            LOGGER.info("Written report to {}", targetFile.getAbsolutePath());
        } catch (final IOException e) {
            throw new ReportException("Failed to write visibility metrics file", e);
        }
    }
}
