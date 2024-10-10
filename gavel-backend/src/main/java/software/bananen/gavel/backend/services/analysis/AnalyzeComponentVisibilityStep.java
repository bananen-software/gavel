package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.VisibilityMetricEntity;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.ComponentVisibility;
import software.bananen.gavel.staticanalysis.ComponentVisibilityMetricsService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class AnalyzeComponentVisibilityStep extends AbstractAnalysisStep {

    private final ComponentVisibilityMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;

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
            final ProjectEntity project) {
        super(taskId, STEP_NAME);
        this.service = requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project = project;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void runAnalysis() {
        final Set<PackageEntity> packages = project.getPackages();

        for (final ComponentVisibility measurement :
                service.measure(projectContext.basePackage(), true)) {
            final PackageEntity existingPackage =
                    findPackageByName(packages, measurement.packageName(), project);

            final VisibilityMetricEntity entity =
                    existingPackage.getVisibilityMetrics()
                            .stream()
                            .findFirst()
                            .orElse(new VisibilityMetricEntity());

            entity.setAverageRelativeVisibility(measurement.averageRelativeVisibility());
            entity.setGlobalRelativeVisibility(measurement.globalRelativeVisibility());
            entity.setRelativeVisibility(measurement.relativeVisibility());

            entity.setPackageField(existingPackage);
            existingPackage.setVisibilityMetrics(new HashSet<>(List.of(entity)));
        }

        project.setPackages(packages);
    }
}
