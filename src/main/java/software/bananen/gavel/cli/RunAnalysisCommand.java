package software.bananen.gavel.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import software.bananen.gavel.context.ProjectContext;
import software.bananen.gavel.context.ProjectContextLoader;
import software.bananen.gavel.detection.CyclicDependencyDetectionService;
import software.bananen.gavel.metrics.*;
import software.bananen.gavel.writer.csv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * A command that can be used to run the configured analysis.
 */
@CommandLine.Command(
        name = "run-analysis",
        mixinStandardHelpOptions = true,
        description = "Runs the configured analysis"
)
public final class RunAnalysisCommand implements Callable<Integer> {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(RunAnalysisCommand.class);

    @CommandLine.Parameters(
            index = "0",
            description = "The config file that should be used for the analysis."
    )
    private File configFile;

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer call() throws Exception {
        final ProjectContext projectContext =
                new ProjectContextLoader().loadProjectContext(configFile);

        if (!projectContext.targetDirectory().exists()) {
            Files.createDirectory(projectContext.targetDirectory().toPath());
        }

        recordCumulativeDependencyMetrics(projectContext);
        recordComponentDependencyMetrics(projectContext);
        recordVisibilityMetrics(projectContext);
        recordDepthOfInheritanceTree(projectContext);

        new CyclicDependencyDetectionService().detect(projectContext.javaClasses(),
                projectContext.basePackage());

        return 0;
    }

    private static void recordDepthOfInheritanceTree(final ProjectContext projectContext) throws IOException {
        final Collection<DepthOfInheritanceTree> measurements =
                new DepthOfInheritanceTreeMetricsService().measure(projectContext.javaClasses());

        final File targetFile = getFileIn(
                projectContext.targetDirectory(),
                "depth-of-inheritance-metrics.csv");

        final CSVWriter writer = new CSVWriter();

        writer.write(targetFile,
                List.of("class", "DIT"),
                List.of(DepthOfInheritanceTree::className,
                        DepthOfInheritanceTree::value),
                measurements);

        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
    }

    private static void recordVisibilityMetrics(final ProjectContext projectContext) throws IOException {
        final Collection<ComponentVisibility> measurements =
                new ComponentVisibilityMetricsService().measure(
                        projectContext.basePackage(),
                        projectContext.config().analysisContext().resolveSubpackages());

        final File targetFile = getFileIn(projectContext.targetDirectory(),
                "visibility-metrics.csv");

        final CSVWriter writer = new CSVWriter();

        writer.write(targetFile,
                List.of("package", "RV", "ARV", "GRV"),
                List.of(ComponentVisibility::packageName,
                        ComponentVisibility::relativeVisibility,
                        ComponentVisibility::averageRelativeVisibility,
                        ComponentVisibility::globalRelativeVisibility),
                measurements);

        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
    }

    private static void recordComponentDependencyMetrics(final ProjectContext projectContext) throws IOException {
        final Collection<ComponentDependency> measurements =
                new ComponentDependencyMetricsService().measure(projectContext.basePackage(),
                        projectContext.config().analysisContext().resolveSubpackages());

        final File targetFile = getFileIn(
                projectContext.targetDirectory(),
                "component-dependency-metrics.csv");

        final CSVWriter writer = new CSVWriter();

        writer.write(targetFile,
                List.of("package", "Ce", "Ca", "I", "A", "D"),
                List.of(ComponentDependency::packageName,
                        ComponentDependency::efferentCoupling,
                        ComponentDependency::afferentCoupling,
                        ComponentDependency::instability,
                        ComponentDependency::abstractness,
                        ComponentDependency::normalizedDistanceFromMainSequence),
                measurements);

        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
    }

    private static void recordCumulativeDependencyMetrics(final ProjectContext projectContext) throws IOException {
        Collection<CumulativeComponentDependency> measurements =
                new CumulativeComponentDependencyMetricsService().measure(
                        List.of(projectContext.basePackage()),
                        projectContext.config().analysisContext().resolveSubpackages());

        File targetFile =
                getFileIn(projectContext.targetDirectory(),
                        "cumulative-dependency-metrics.csv");

        CSVWriter out = new CSVWriter();

        out.write(targetFile,
                List.of("package", "CCD", "ACD", "RACD", "NCCD"),
                List.of(CumulativeComponentDependency::packageName,
                        CumulativeComponentDependency::cumulative,
                        CumulativeComponentDependency::average,
                        CumulativeComponentDependency::relativeAverage,
                        CumulativeComponentDependency::normalized),
                measurements);
        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
    }

    private static File getFileIn(File targetDirectory, String fileName) {
        return Paths.get(targetDirectory.getPath(), fileName).toFile();
    }
}
