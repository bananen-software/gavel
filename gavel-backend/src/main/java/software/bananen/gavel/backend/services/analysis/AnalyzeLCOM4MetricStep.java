package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.services.domain.ClassCohesionService;
import software.bananen.gavel.backend.services.domain.ClassService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.LCOM4Metric;
import software.bananen.gavel.staticanalysis.LCOM4MetricsService;

public class AnalyzeLCOM4MetricStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze LCOM4";
    private final LCOM4MetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;
    private final PackageService packageService;
    private final ClassService classService;
    private final ClassCohesionService cohesionService;

    /**
     * Creates a new instance.
     *
     * @param taskId The ID of the task that the step belongs to.
     */
    public AnalyzeLCOM4MetricStep(final String taskId,
                                  final LCOM4MetricsService service,
                                  final ProjectContext projectContext,
                                  final ProjectEntity project,
                                  final PackageService packageService,
                                  final ClassService classService,
                                  final ClassCohesionService cohesionService) {
        super(taskId, STEP_NAME);

        this.service = service;
        this.projectContext = projectContext;
        this.project = project;
        this.packageService = packageService;
        this.classService = classService;
        this.cohesionService = cohesionService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final LCOM4Metric metric : service.measure(projectContext.javaClasses())) {
            final PackageEntity packageEntity =
                    packageService.findOrCreatePackage(project, metric.packageName());
            final ClassEntity classEntity =
                    classService.findOrCreateClass(packageEntity, metric.className());

            cohesionService.createOrUpdate(classEntity, metric.value());
        }
    }
}
