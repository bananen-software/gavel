package software.bananen.gavel.backend.services.analysis;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.backend.services.domain.PackageComponentDependencyMetricsService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.ComponentDependency;
import software.bananen.gavel.staticanalysis.ComponentDependencyMetricsService;

import static java.util.Objects.requireNonNull;

/**
 * A step that can be used to analyze the component dependencies of a project.
 */
public class AnalyzeComponentDependenciesStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze component dependencies";

    private final ComponentDependencyMetricsService service;
    private final ProjectContext projectContext;
    private final JpaProjectEntity project;
    private final PackageService packageService;
    private final PackageComponentDependencyMetricsService componentDependencyService;

    /**
     * Creates a new instance.
     *
     * @param service                    The service that should be used by the step.
     * @param projectContext             The project context that should be analyzed.
     * @param project                    The project that should be analyzed.
     * @param packageService             The package service.
     * @param componentDependencyService The component dependency service.
     */
    public AnalyzeComponentDependenciesStep(
            final ComponentDependencyMetricsService service,
            final ProjectContext projectContext,
            final JpaProjectEntity project,
            final PackageService packageService,
            final PackageComponentDependencyMetricsService componentDependencyService) {
        super(STEP_NAME);

        this.service =
                requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project =
                requireNonNull(project, "The project may not be null");
        this.packageService =
                requireNonNull(packageService, "The package service may not be null");
        this.componentDependencyService =
                requireNonNull(componentDependencyService, "The component dependency metrics service may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final ComponentDependency measurement : service.measure(projectContext.basePackage(), true)) {
            final JpaPackageEntity existingPackage =
                    packageService.findOrCreatePackage(project, measurement.packageName());

            componentDependencyService.createOrUpdate(existingPackage, measurement);
        }
    }
}
