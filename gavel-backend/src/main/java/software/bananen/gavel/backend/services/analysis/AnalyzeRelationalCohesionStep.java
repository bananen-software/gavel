package software.bananen.gavel.backend.services.analysis;

import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.RelationalCohesionMetricEntity;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.staticanalysis.RelationalCohesion;
import software.bananen.gavel.staticanalysis.RelationalCohesionMetricsService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class AnalyzeRelationalCohesionStep extends AbstractAnalysisStep {

    private static final String STEP_NAME = "Analyze relative cohesion";
    private final RelationalCohesionMetricsService service;
    private final ProjectContext projectContext;
    private final ProjectEntity project;

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
            final ProjectEntity project) {
        super(taskId, STEP_NAME);
        this.service = requireNonNull(service, "The service may not be null");
        this.projectContext =
                requireNonNull(projectContext, "The project context may not be null");
        this.project = project;
    }

    @Override
    protected void runAnalysis() {
        //TODO: Store results in database
        final Set<PackageEntity> packages = project.getPackages();

        for (final RelationalCohesion measurement :
                service.measure(List.of(projectContext.basePackage()))) {
            final PackageEntity matchingPackage =
                    findPackageByName(packages, measurement.packageName(), project);

            final RelationalCohesionMetricEntity entity =
                    matchingPackage.getRelationalCohesionMetrics()
                            .stream()
                            .findFirst()
                            .orElse(new RelationalCohesionMetricEntity());

            entity.setStatus("UNKNOWN");
            entity.setRelationalCohesion(measurement.relationalCohesion());
            entity.setNumberOfInternalRelationships(measurement.numberOfInternalRelationships());
            entity.setNumberOfTypes(measurement.numberOfTypes());

            entity.setPackageField(matchingPackage);

            matchingPackage.setRelationalCohesionMetrics(new HashSet<>(List.of(entity)));
        }

        project.setPackages(packages);
    }
}
