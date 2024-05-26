package software.bananen.gavel.reports.json;

import software.bananen.gavel.metrics.*;
import software.bananen.gavel.reports.Report;
import software.bananen.gavel.reports.ReportFactory;

import java.io.File;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * A {@link ReportFactory} that can be used to generate JSON based report files.
 */
public final class JsonFileReportFactory implements ReportFactory {

    private final File targetDirectory;

    /**
     * Creates a new instance.
     *
     * @param targetDirectory The target directory.
     */
    public JsonFileReportFactory(final File targetDirectory) {

        this.targetDirectory =
                requireNonNull(targetDirectory, "The target directory may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<ComponentDependency>> createComponentDependencyReport() {
        return new GenericJsonFileReport<>(targetDirectory, "component-dependencies.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<CumulativeComponentDependency>> createCumulativeComponentDependencyReport() {
        return new GenericJsonFileReport<>(targetDirectory, "cumulative-component-dependencies.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<DepthOfInheritanceTree>> createDepthOfInheritanceTreeReport() {
        return new GenericJsonFileReport<>(targetDirectory, "depth-of-inheritance-tree.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<ComponentVisibility>> createComponentVisibilityReport() {
        return new GenericJsonFileReport<>(targetDirectory, "component-visibility.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<RelationalCohesion>> createRelationalCohesionReport() {
        return new GenericJsonFileReport<>(targetDirectory, "relational-cohesion.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<AuthorComplexityHistoryEntry>> createAuthorComplexityHistoryReport() {
        return new GenericJsonFileReport<>(targetDirectory, "author-comlexity-history.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<CodeHotspot>> createCodeHotspotReport() {
        return new GenericJsonFileReport<>(targetDirectory, "code-hotspots.json");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Report<Collection<ChangeCouplingMetric>> createChangeCouplingMetricReport() {
        return new GenericJsonFileReport<>(targetDirectory, "change-coupling.json");
    }
}
