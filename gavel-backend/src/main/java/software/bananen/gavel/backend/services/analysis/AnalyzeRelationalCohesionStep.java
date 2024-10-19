package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.services.domain.PackageRelationalCohesionMetricsService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.RelationalCohesion;
import software.bananen.gavel.staticanalysis.RelationalCohesionMetricsService;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class AnalyzeRelationalCohesionStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze relative cohesion";
    private final RelationalCohesionMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;
    private final PackageService packageService;
    private final PackageRelationalCohesionMetricsService relationalCohesionService;

    /**
     * Creates a new instance.
     *
     * @param taskId         The ID of the task.
     * @param service        The service that should be used by the step.
     * @param projectContext The project context that should be analyzed.
     */
    public AnalyzeRelationalCohesionStep(
            final String taskId,
            final RelationalCohesionMetricsService service,
            final ProjectContext projectContext,
            final ProjectEntity project,
            final PackageService packageService,
            final PackageRelationalCohesionMetricsService relationalCohesionService) {
        super(taskId, STEP_NAME);
        this.service = requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project = project;
        this.packageService = packageService;
        this.relationalCohesionService = relationalCohesionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final RelationalCohesion measurement :
                service.measure(List.of(projectContext.basePackage()))) {
            final PackageEntity packageEntity =
                    packageService.findOrCreatePackage(project, measurement.packageName());

            relationalCohesionService.createOrUpdate(packageEntity, measurement);
        }
    }
}
