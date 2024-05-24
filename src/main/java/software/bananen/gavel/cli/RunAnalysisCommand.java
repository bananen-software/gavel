package software.bananen.gavel.cli;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import software.bananen.gavel.config.json.GitConfig;
import software.bananen.gavel.context.ProjectContext;
import software.bananen.gavel.context.ProjectContextLoader;
import software.bananen.gavel.metrics.*;
import software.bananen.gavel.metrics.git.GitUtil;
import software.bananen.gavel.metrics.git.Mailmap;
import software.bananen.gavel.reports.ReportChain;
import software.bananen.gavel.reports.ReportException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static software.bananen.gavel.metrics.git.GitUtil.loadMailmap;

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
        recordRelationalCohesionMetrics(projectContext);

        /*new CyclicDependencyDetectionService().detect(projectContext.javaClasses(),
                projectContext.basePackage());*/

        measureGitBasedMetrics(projectContext);

        return 0;
    }

    /**
     * Measures the git based metrics of the codebase.
     *
     * @param projectContext The project context.
     * @throws IOException     Might be thrown in case that files could not be
     *                         read.
     * @throws GitAPIException Might be thrown in case that reading the git
     *                         repository failed
     * @throws ReportException Might be thrown in case that the report could
     *                         not be written.
     */
    private static void measureGitBasedMetrics(ProjectContext projectContext) throws IOException, GitAPIException, ReportException {
        final HotspotMetricsService hotspotMetricsService = new HotspotMetricsService();

        final AuthorComplexityHistory authorComplexityHistory =
                new AuthorComplexityHistory();

        final FileComplexityHistory history = new FileComplexityHistory();
        final ChangeCouplingStore changeCouplingStore = new ChangeCouplingStore();

        for (final Path path :
                hotspotMetricsService.locateGitRepositories(projectContext.config().analysisContext().includedPaths())) {
            try (final Repository repository = hotspotMetricsService.loadRepository(path)) {
                final String projectName = path.getName(path.getNameCount() - 1).toString();

                final Mailmap mailmap = loadMailmap(path);

                LOGGER.info(".mailmap loaded with {} entries", mailmap.size());

                final Git git = new Git(repository);

                final RevCommit latestCommit = new Git(repository).
                        log().
                        setMaxCount(1).
                        call().
                        iterator().
                        next();

                final LocalDateTime endTimestamp =
                        GitUtil.extractTimestampFrom(latestCommit);

                final LocalDateTime startTimestamp =
                        endTimestamp.minusMonths(projectContext.config().analysisContext().gitConfig().analyzedMonths());

                LOGGER.info("Analyzing commits from {} to {}", startTimestamp, endTimestamp);

                final List<RevCommit> commits = GitUtil.getCommitsFromOldToNew(git)
                        .stream()
                        .filter(commit -> GitUtil.between(commit, startTimestamp, endTimestamp))
                        .toList();

                LOGGER.info("Found {} commits for the time period", commits.size());

                for (final RevCommit commit : commits) {
                    final Author author = mailmap.map(GitUtil.extractAuthor(commit));

                    final List<String> touchedFiles = new ArrayList<>();

                    for (final DiffEntry diff : GitUtil.extractDiffEntries(repository, commit)) {
                        final GitConfig gitConfig =
                                projectContext.config().analysisContext().gitConfig();

                        touchedFiles.add(diff.getNewPath());

                        if (isNotExcludedFileType(diff, gitConfig)) {
                            final String content =
                                    GitUtil.loadFileContentFromRevision(repository, commit, diff);

                            final int complexity = content.lines()
                                    .map(GitUtil::calculateWhitespaceComplexity)
                                    .mapToInt(Integer::intValue)
                                    .sum();

                            final FileComplexityHistoryEntry addedFileComplexity =
                                    history.addFileComplexity(new FileComplexity(
                                                    projectName,
                                                    diff.getNewPath(),
                                                    content.lines().count(),
                                                    complexity
                                            ),
                                            GitUtil.extractTimestampFrom(commit)
                                    );

                            authorComplexityHistory.addComplexity(
                                    author,
                                    addedFileComplexity
                            );
                        }
                    }

                    for (int i = 0; i < touchedFiles.size(); i++) {
                        for (int j = 0; j < touchedFiles.size(); j++) {
                            if (i != j) {
                                changeCouplingStore.record(
                                        touchedFiles.get(i),
                                        touchedFiles.get(j)
                                );
                            }
                        }
                    }
                }
            }
        }

        //TODO: Report origin complexity history

        ReportChain.measure(history::listHotspots)
                .andReport(projectContext.reportFactory().createCodeHotspotReport());

        ReportChain.measure(authorComplexityHistory::list)
                .andReport(projectContext.reportFactory().createAuthorComplexityHistoryReport());

        ReportChain.measure(changeCouplingStore::list)
                .andReport(projectContext.reportFactory().createChangeCouplingMetricReport());
    }


    private static boolean isNotExcludedFileType(final DiffEntry diff,
                                                 final GitConfig gitConfig) {
        return gitConfig.includedFileExtensions()
                .stream()
                .anyMatch(ext -> diff.getNewPath().endsWith("." + ext));
    }

    private static void recordDepthOfInheritanceTree(final ProjectContext projectContext)
            throws ReportException {
        final DepthOfInheritanceTreeMetricsService service = new DepthOfInheritanceTreeMetricsService();

        ReportChain.measure(() -> service.measure(projectContext.javaClasses(),
                        projectContext.config().analysisContext().metricsConfig().depthOfInheritanceConfig().threshold()))
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
