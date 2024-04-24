package software.bananen.gavel.cli;

import picocli.CommandLine;
import software.bananen.gavel.context.ProjectContext;
import software.bananen.gavel.context.ProjectContextLoader;
import software.bananen.gavel.detection.CyclicDependencyDetectionService;
import software.bananen.gavel.metrics.*;
import software.bananen.gavel.reports.ReportChain;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.nio.file.Files;
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
        recordRelationalCohesionMetrics(projectContext);

        new CyclicDependencyDetectionService().detect(projectContext.javaClasses(),
                projectContext.basePackage());

        return 0;
    }

    private static void recordDepthOfInheritanceTree(final ProjectContext projectContext)
            throws ReportException {
        final DepthOfInheritanceTreeMetricsService service = new DepthOfInheritanceTreeMetricsService();

        ReportChain.measure(() -> service.measure(projectContext.javaClasses()))
                .andReport(projectContext.reportFactory().createDepthOfInheritanceTreeReport());
    }

    private static void recordVisibilityMetrics(final ProjectContext projectContext)
            throws ReportException {
        final ComponentVisibilityMetricsService service = new ComponentVisibilityMetricsService();

        ReportChain.measure(() ->
                        service.measure(
                                projectContext.basePackage(),
                                projectContext.config().analysisContext().resolveSubpackages()))
                .andReport(projectContext.reportFactory().createComponentVisibilityReport());
    }

    private static void recordComponentDependencyMetrics(final ProjectContext projectContext)
            throws ReportException {
        final ComponentDependencyMetricsService service = new ComponentDependencyMetricsService();

        ReportChain.measure(() ->
                        service.measure(projectContext.basePackage(),
                                projectContext.config().analysisContext().resolveSubpackages()))
                .andReport(projectContext.reportFactory().createComponentDependencyReport());
    }

    private static void recordCumulativeDependencyMetrics(final ProjectContext projectContext)
            throws ReportException {
        final CumulativeComponentDependencyMetricsService service = new CumulativeComponentDependencyMetricsService();

        ReportChain.measure(() ->
                        service.measure(
                                List.of(projectContext.basePackage()),
                                projectContext.config().analysisContext().resolveSubpackages()))
                .andReport(projectContext.reportFactory().createCumulativeComponentDependencyReport());
    }

    private static void recordRelationalCohesionMetrics(final ProjectContext projectContext)
            throws ReportException {
        final RelationalCohesionMetricsService service = new RelationalCohesionMetricsService();

        ReportChain.measure(() ->
                        service.measure(
                                List.of(projectContext.basePackage())))
                .andReport(projectContext.reportFactory().createRelationalCohesionReport());
    }
}
