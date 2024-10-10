package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.ComponentDependencyMetricEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.ComponentDependency;
import software.bananen.gavel.staticanalysis.ComponentDependencyMetricsService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class AnalyzeComponentDependenciesStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze component dependencies";

    private final ComponentDependencyMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;

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
        final Collection<ComponentDependency> measurements =
                service.measure(projectContext.basePackage(), true);

        final Set<PackageEntity> packages = project.getPackages();

        for (final ComponentDependency measurement : measurements) {
            final PackageEntity existingPackage =
                    findPackageByName(packages, measurement.packageName(), project);

            final ComponentDependencyMetricEntity componentDependencyMetrics =
                    existingPackage.getComponentDependencyMetrics()
                            .stream()
                            .findFirst()
                            .orElse(new ComponentDependencyMetricEntity());

            componentDependencyMetrics.setAbstractness(measurement.abstractness());
            componentDependencyMetrics.setInstability(measurement.instability());
            componentDependencyMetrics.setDistance(measurement.normalizedDistanceFromMainSequence());
            componentDependencyMetrics.setAfferentCoupling(measurement.afferentCoupling());
            componentDependencyMetrics.setEfferentCoupling(measurement.efferentCoupling());

            componentDependencyMetrics.setPackageField(existingPackage);
            existingPackage.setComponentDependencyMetrics(
                    new HashSet<>(Set.of(componentDependencyMetrics)));
        }

        project.setPackages(packages);
    }
}
