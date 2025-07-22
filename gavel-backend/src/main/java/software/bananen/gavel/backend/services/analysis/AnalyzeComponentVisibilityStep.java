package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.services.domain.PackageService;
import software.bananen.gavel.backend.services.domain.PackageVisibilityMetricsService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaPackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectEntity;
import software.bananen.gavel.staticanalysis.ComponentVisibility;
import software.bananen.gavel.staticanalysis.ComponentVisibilityMetricsService;

import static java.util.Objects.requireNonNull;

/**
 * A step that can be used to analyze the component visibility of a project.
 */
public class AnalyzeComponentVisibilityStep extends AbstractAnalysisStep {

    private final ComponentVisibilityMetricsService service;
    private final ProjectContext projectContext;
    private final JpaProjectEntity project;
    private final PackageService packageService;
    private final PackageVisibilityMetricsService visibilityService;

    private static final String STEP_NAME = "Analyze component visibility";

    /**
     * Creates a new instance.
     *
     * @param service           The service that this step should use.
     * @param projectContext    The project context that should be analyzed.
     * @param project
     * @param packageService
     * @param visibilityService
     */
    public AnalyzeComponentVisibilityStep(
            final ComponentVisibilityMetricsService service,
            final ProjectContext projectContext,
            final JpaProjectEntity project,
            final PackageService packageService,
            final PackageVisibilityMetricsService visibilityService) {
        super(STEP_NAME);

        this.service =
                requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project =
                requireNonNull(project, "The project may not be null");
        this.packageService =
                requireNonNull(packageService, "The package service may not be null");
        this.visibilityService =
                requireNonNull(visibilityService, "The visibility service may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        for (final ComponentVisibility measurement :
                service.measure(projectContext.basePackage(), true)) {
            final JpaPackageEntity existingPackage =
                    packageService.findOrCreatePackage(project, measurement.packageName());

            visibilityService.saveOrUpdate(existingPackage, measurement);
        }
    }
}
