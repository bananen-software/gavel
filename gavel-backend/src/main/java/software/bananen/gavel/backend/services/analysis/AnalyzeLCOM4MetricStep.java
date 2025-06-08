package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.services.domain.ClassCohesionService;
import software.bananen.gavel.backend.services.domain.ClassService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;
import software.bananen.gavel.staticanalysis.LCOM4Metric;
import software.bananen.gavel.staticanalysis.LCOM4MetricsService;

import static java.util.Objects.requireNonNull;

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
     * @param service
     * @param projectContext
     * @param project
     * @param packageService
     * @param classService
     * @param cohesionService
     */
    public AnalyzeLCOM4MetricStep(final LCOM4MetricsService service,
                                  final ProjectContext projectContext,
                                  final ProjectEntity project,
                                  final PackageService packageService,
                                  final ClassService classService,
                                  final ClassCohesionService cohesionService) {
        super(STEP_NAME);

        this.service =
                requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The projectContext may not be null");
        this.project =
                requireNonNull(project, "The project may not be null");
        this.packageService =
                requireNonNull(packageService, "The packageService may not be null");
        this.classService =
                requireNonNull(classService, "The classService may not be null");
        this.cohesionService =
                requireNonNull(cohesionService, "The cohesionService may not be null");
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
