package software.bananen.gavel.reports.csv;

import software.bananen.gavel.metrics.*;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportFactory;

import java.io.File;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * A {@link ReportFactory} that generates CSV based reports.
 */
public final class CSVReportFactory implements ReportFactory {

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public CSVReportFactory(final File targetDirectory) {

        this.targetDirectory =
                requireNonNull(targetDirectory, "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<ComponentDependency>> createComponentDependencyReport() {
        return new CSVComponentDependencyReport(targetDirectory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<CumulativeComponentDependency>> createCumulativeComponentDependencyReport() {
        return new CSVCumulativeComponentDependencyReport(targetDirectory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<DepthOfInheritanceTree>> createDepthOfInheritanceTreeReport() {
        return new CSVDepthOfInheritanceTreeReport(targetDirectory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<ComponentVisibility>> createComponentVisibilityReport() {
        return new CSVVisibilityMetricsReport(targetDirectory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<RelationalCohesion>> createRelationalCohesionReport() {
        return new CSVRelationalCohesionReport(targetDirectory);
    }
}
