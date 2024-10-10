package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.CumulativeComponentDependencyEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.CumulativeComponentDependency;
import software.bananen.gavel.staticanalysis.CumulativeComponentDependencyMetricsService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class AnalyzeCumulativeComponentDependencyStep extends AbstractAnalysisStep {
    private static final String STEP_NAME = "Analyze cumulative component dependency";
    private final CumulativeComponentDependencyMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;

    /**
     * Creates a new instance.
     *
     * @param taskId         The ID of the task.
     * @param service        The service that should be used by the step.
     * @param projectContext The project context that should be analyzed.
     */
    public AnalyzeCumulativeComponentDependencyStep(
            final String taskId,
            final CumulativeComponentDependencyMetricsService service,
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

        for (final CumulativeComponentDependency measurement :
                service.measure(List.of(projectContext.basePackage()), true)) {
            final PackageEntity existingPackage =
                    findPackageByName(packages, measurement.packageName(), project);

            final CumulativeComponentDependencyEntity entity = existingPackage.getCumulativeComponentDependencies()
                    .stream()
                    .findFirst()
                    .orElse(new CumulativeComponentDependencyEntity());

            entity.setCumulative(measurement.cumulative());
            entity.setAverage(measurement.average());
            entity.setNormalized(measurement.normalized());
            entity.setRelativeAverage(measurement.relativeAverage());

            entity.setPackageField(existingPackage);
            existingPackage.setCumulativeComponentDependencies(new HashSet<>(List.of(entity)));
        }

        project.setPackages(packages);
    }
}
