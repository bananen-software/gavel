package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.backend.services.domain.PackageVisibilityMetricsService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.ComponentVisibility;
import software.bananen.gavel.staticanalysis.ComponentVisibilityMetricsService;

import static java.util.Objects.requireNonNull;

public class AnalyzeComponentVisibilityStep extends AbstractAnalysisStep {

    private final ComponentVisibilityMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;
    private final PackageService packageService;
    private final PackageVisibilityMetricsService visibilityService;

    private static final String STEP_NAME = "Analyze component visibility";

    /**
     * Creates a new instance.
     *
     * @param taskId         The ID of the task that the step belongs to.
     * @param service        The service that this step should use.
     * @param projectContext The project context that should be analyzed.
     */
    public AnalyzeComponentVisibilityStep(
            final String taskId,
            final ComponentVisibilityMetricsService service,
            final ProjectContext projectContext,
            final ProjectEntity project,
            final PackageService packageService,
            final PackageVisibilityMetricsService visibilityService) {
        super(taskId, STEP_NAME);
        this.service = requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project = project;
        this.packageService = packageService;
        this.visibilityService = visibilityService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final ComponentVisibility measurement :
                service.measure(projectContext.basePackage(), true)) {
            final PackageEntity existingPackage =
                    packageService.findOrCreatePackage(project, measurement.packageName());

            visibilityService.saveOrUpdate(existingPackage, measurement);
        }
    }
}
