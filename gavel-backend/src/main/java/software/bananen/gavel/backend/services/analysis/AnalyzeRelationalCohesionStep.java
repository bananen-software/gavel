package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.services.domain.PackageRelationalCohesionMetricsService;
import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaPackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.staticanalysis.RelationalCohesion;
import software.bananen.gavel.staticanalysis.RelationalCohesionMetricsService;

import java.util.List;

import static java.util.Objects.requireNonNull;

public class AnalyzeRelationalCohesionStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze relative cohesion";
    private final RelationalCohesionMetricsService service;
    private final ProjectContext projectContext;
    private final JpaProjectEntity project;
    private final PackageService packageService;
    private final PackageRelationalCohesionMetricsService relationalCohesionService;

    /**
     * Creates a new instance.
     *
     * @param service        The service that should be used by the step.
     * @param projectContext The project context that should be analyzed.
     */

    /**
     * Creates a new instance.
     *
     * @param service                   The service that should be used by the step.
     * @param projectContext            The project context that should be analyzed.
     * @param project
     * @param packageService
     * @param relationalCohesionService
     */
    public AnalyzeRelationalCohesionStep(
            final RelationalCohesionMetricsService service,
            final ProjectContext projectContext,
            final JpaProjectEntity project,
            final PackageService packageService,
            final PackageRelationalCohesionMetricsService relationalCohesionService) {
        super(STEP_NAME);

        this.service =
                requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project =
                requireNonNull(project, "The project may not be null");
        this.packageService =
                requireNonNull(packageService, "The package service may not be null");
        this.relationalCohesionService =
                requireNonNull(relationalCohesionService, "The relational cohesion metrics service may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final RelationalCohesion measurement :
                service.measure(List.of(projectContext.basePackage()))) {
            final JpaPackageEntity packageEntity =
                    packageService.findOrCreatePackage(project, measurement.packageName());

            relationalCohesionService.createOrUpdate(packageEntity, measurement);
        }
    }
}
