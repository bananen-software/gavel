package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.services.domain.PackageComponentDependencyMetricsService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.ComponentDependency;
import software.bananen.gavel.staticanalysis.ComponentDependencyMetricsService;

import static java.util.Objects.requireNonNull;

public class AnalyzeComponentDependenciesStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze component dependencies";

    private final ComponentDependencyMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;
    private final PackageService packageService;
    private final PackageComponentDependencyMetricsService componentDependencyService;

    /**
     * Creates a new instance.
     *
     * @param taskId         The ID of the task.
     * @param service        The service that should be used by the step.
     * @param projectContext The project context that should be analyzed.
     * @param project
     */
    public AnalyzeComponentDependenciesStep(
            final String taskId,
            final ComponentDependencyMetricsService service,
            final ProjectContext projectContext,
            final ProjectEntity project,
            final PackageService packageService,
            final PackageComponentDependencyMetricsService componentDependencyService) {
        super(taskId, STEP_NAME);

        this.service = requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project = project;
        this.packageService = packageService;
        this.componentDependencyService = componentDependencyService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final ComponentDependency measurement : service.measure(projectContext.basePackage(), true)) {
            final PackageEntity existingPackage =
                    packageService.findOrCreatePackage(project, measurement.packageName());

            componentDependencyService.createOrUpdate(existingPackage, measurement);
        }
    }
}
