package software.bananen.gavel.backend.services.analysis;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.backend.services.domain.PackageCumulativeComponentDependencyMetricsService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.CumulativeComponentDependency;
import software.bananen.gavel.staticanalysis.CumulativeComponentDependencyMetricsService;

import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * A step that can be used to analyze the cumulative component dependencies of
 * a project.
 */
public class AnalyzeCumulativeComponentDependencyStep extends AbstractAnalysisStep {
    private static final String STEP_NAME = "Analyze cumulative component dependency";
    private final CumulativeComponentDependencyMetricsService service;
    private final ProjectContext projectContext;
    private final JpaProjectEntity project;
    private final PackageService packageService;
    private final PackageCumulativeComponentDependencyMetricsService cumulativeComponentDependencyService;

    /**
     * Creates a new instance.
     *
     * @param service        The service that should be used by the step.
     * @param projectContext The project context that should be analyzed.
     */
    public AnalyzeCumulativeComponentDependencyStep(
            final CumulativeComponentDependencyMetricsService service,
            final ProjectContext projectContext,
            final JpaProjectEntity project,
            final PackageService packageService,
            final PackageCumulativeComponentDependencyMetricsService cumulativeComponentDependencyService) {
        super(STEP_NAME);

        this.service = requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project = project;
        this.packageService = packageService;
        this.cumulativeComponentDependencyService = cumulativeComponentDependencyService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final CumulativeComponentDependency measurement :
                service.measure(List.of(projectContext.basePackage()), true)) {
            final JpaPackageEntity existingPackage =
                    packageService.findOrCreatePackage(project, measurement.packageName());

            cumulativeComponentDependencyService.createOrUpdate(existingPackage, measurement);
        }
    }
}
