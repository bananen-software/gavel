package software.bananen.gavel.backend.services.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * An abstract base class that can be used to implement analysis steps.
 */
public abstract class AbstractAnalysisStep implements Runnable {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractAnalysisStep.class);
    private final String taskId;
    private final String stepName;

    /**
     * Creates a new instance.
     *
     * @param taskId   The ID of the task that the step belongs to.
     * @param stepName The name of the step.
     */
    public AbstractAnalysisStep(
            final String taskId,
            final String stepName) {
        this.taskId = taskId;
        this.stepName = stepName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void run() {
        final StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        LOGGER.info("[Task: {}] Running step: {}", taskId, stepName);
        runAnalysis();
        stopWatch.stop();
        LOGGER.info("[Task: {}] Completed Step: {} completed in {}ms", taskId, stepName, stopWatch.getTotalTimeMillis());
    }

    /**
     * Runs the analysis of the step.
     */
    protected abstract void runAnalysis();

    /**
     * Maps the given measurement and packages to a {@link PackageEntity}
     *
     * @param packageName The package name
     * @param packages    The packages.
     * @return The mapping function.
     */
    protected Supplier<PackageEntity> mapToEntity(
            final String packageName,
            final Set<PackageEntity> packages,
            final ProjectEntity project) {
        return () -> {
            final PackageEntity pkg = new PackageEntity();

            pkg.setPackageName(packageName);
            pkg.setProject(project);
            pkg.setClasses(new HashSet<>());
            pkg.setComponentDependencyMetrics(new HashSet<>());
            pkg.setCumulativeComponentDependencies(new HashSet<>());
            pkg.setVisibilityMetrics(new HashSet<>());
            packages.add(pkg);

            return pkg;
        };
    }

    protected PackageEntity findPackageByName(final Set<PackageEntity> packages,
                                              final String packageName,
                                              final ProjectEntity project) {
        return packages.stream()
                .filter(pkg -> Objects.equals(pkg.getPackageName(), packageName))
                .findFirst()
                .orElseGet(mapToEntity(packageName, packages, project));
    }
}
