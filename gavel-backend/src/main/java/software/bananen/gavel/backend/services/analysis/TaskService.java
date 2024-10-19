package software.bananen.gavel.backend.services.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.WorkspaceEntity;
import software.bananen.gavel.backend.repository.ProjectRepository;
import software.bananen.gavel.backend.repository.WorkspaceRepository;
import software.bananen.gavel.backend.services.domain.*;
import software.bananen.gavel.behavioralanalysis.git.GitService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.contextloader.ProjectContextData;
import software.bananen.gavel.contextloader.ProjectContextLoader;
import software.bananen.gavel.contextloader.ProjectContextLoaderException;
import software.bananen.gavel.staticanalysis.*;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * A service that can be used to execute an analysis.
 */
@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private static final DepthOfInheritanceTreeMetricsService DEPTH_OF_INHERITANCE_SERVICE =
            new DepthOfInheritanceTreeMetricsService();

    private static final ComponentVisibilityMetricsService COMPONENT_VISIBILITY_SERVICE =
            new ComponentVisibilityMetricsService();

    private static final CumulativeComponentDependencyMetricsService CUMULATIVE_COMPONENT_DEPENDENCY_SERVICE =
            new CumulativeComponentDependencyMetricsService();

    private static final RelationalCohesionMetricsService RELATIVE_COHESION_SERVICE =
            new RelationalCohesionMetricsService();

    private static final ComponentDependencyMetricsService COMPONENT_DEPENDENCY_METRICS_SERVICE =
            new ComponentDependencyMetricsService();

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final AuthorService authorService;
    private final PackageService packageService;
    private final ClassService classService;
    private final ClassContributionService classContributionService;
    private final ClassCohesionService cohesionService;
    private final PackageVisibilityMetricsService packageVisibilityMetricsService;
    private final PackageComponentDependencyMetricsService packageComponentDependencyMetricsService;
    private final PackageCumulativeComponentDependencyMetricsService packageCumulativeComponentDependencyMetricsService;
    private final PackageRelationalCohesionMetricsService relationalCohesionMetricsService;
    private final ClassLinesOfCodeService classLinesOfCodeService;
    private final ClassComplexityService classComplexityService;
    private final PackageComplexityService packageComplexityService;
    private final PackageLinesOfCodeService packageLinesOfCodeService;
    private final ProjectFileService projectFileService;

    public TaskService(@Autowired final WorkspaceRepository workspaceRepository,
                       @Autowired final ProjectRepository projectRepository,
                       @Autowired final AuthorService authorService,
                       @Autowired final PackageService packageService,
                       @Autowired final ClassService classService,
                       @Autowired final ClassContributionService classContributionService,
                       @Autowired final ClassCohesionService cohesionService,
                       @Autowired final PackageVisibilityMetricsService packageVisibilityMetricsService,
                       @Autowired final PackageComponentDependencyMetricsService packageComponentDependencyMetricsService,
                       @Autowired final PackageCumulativeComponentDependencyMetricsService packageCumulativeComponentDependencyMetricsService,
                       @Autowired final PackageRelationalCohesionMetricsService relationalCohesionMetricsService,
                       @Autowired final ClassLinesOfCodeService classLinesOfCodeService,
                       @Autowired final ClassComplexityService classComplexityService,
                       @Autowired final PackageComplexityService packageComplexityService,
                       @Autowired final PackageLinesOfCodeService packageLinesOfCodeService,
                       @Autowired final ProjectFileService projectFileService) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.authorService = authorService;
        this.packageService = packageService;
        this.classService = classService;
        this.classContributionService = classContributionService;
        this.cohesionService = cohesionService;
        this.packageVisibilityMetricsService = packageVisibilityMetricsService;
        this.packageComponentDependencyMetricsService = packageComponentDependencyMetricsService;
        this.packageCumulativeComponentDependencyMetricsService = packageCumulativeComponentDependencyMetricsService;
        this.relationalCohesionMetricsService = relationalCohesionMetricsService;
        this.classLinesOfCodeService = classLinesOfCodeService;
        this.classComplexityService = classComplexityService;
        this.packageComplexityService = packageComplexityService;
        this.packageLinesOfCodeService = packageLinesOfCodeService;
        this.projectFileService = projectFileService;
    }

    @Transactional
    public void runAnalysis(final String taskId) {
        try {
            final WorkspaceEntity workspace =
                    workspaceRepository.findAll()
                            .stream()
                            .findFirst()
                            .orElseGet(() -> {
                                final WorkspaceEntity ws = new WorkspaceEntity();
                                ws.setName("Pendenzenliste");
                                ws.setPath("/home/dennis/workspace/github/flens-dev/pendenzenliste");
                                ws.setExcludedPath(List.of("/test/", "/integrationTest/", "/generated/"));
                                ws.setBasePackage("pendenzenliste");
                                return ws;
                            });

            if (workspace.getId() == null) {
                workspaceRepository.save(workspace);
            }

            for (final Path projectPath :
                    new GitService().locateGitRepositories(
                            List.of(workspace.getPath()))) {

                final String projectName =
                        projectPath.getName(projectPath.getNameCount() - 1).toString();

                final ProjectContextData projectContextData =
                        new ProjectContextData(
                                projectPath.toString(),
                                workspace.getExcludedPath(),
                                workspace.getBasePackage()
                        );

                LOGGER.info("[Task: {}] Loading project context {}", taskId, projectContextData);

                final ProjectContext projectContext =
                        new ProjectContextLoader().loadProjectContext(projectContextData);
                LOGGER.info("[Task: {}] Loaded project context", taskId);

                final ProjectEntity projectEntity =
                        projectRepository.findByWorkspaceAndName(workspace, projectName)
                                .orElseGet(() -> {
                                    final ProjectEntity newProject = new ProjectEntity();
                                    newProject.setName(projectName);
                                    workspace.getProjects().add(newProject);
                                    newProject.setWorkspace(workspace);
                                    newProject.setPath(projectPath.toString());
                                    return newProject;
                                });

                LOGGER.info("[Task: {}] Starting analysis", taskId);
                assembleSteps(taskId, projectContext, projectEntity)
                        .forEach(AbstractAnalysisStep::run);
                LOGGER.info("[Task: {}] Completed analysis", taskId);

                LOGGER.info("[Task: {}] Storing results", taskId);
                projectRepository.save(projectEntity);
                workspaceRepository.saveAndFlush(workspace);
                LOGGER.info("[Task: {}] Stored results", taskId);
            }
        } catch (final ProjectContextLoaderException e) {
            throw new RuntimeException(e);
        } catch (final Throwable e) {
            LOGGER.error("Analysis failed", e);
            throw e;
        }
    }

    /**
     * Assembles the steps that are executed to analyze the project.
     *
     * @param taskId         The ID of the task.
     * @param projectContext The project context.
     * @return The assembled steps.
     */
    private Collection<AbstractAnalysisStep> assembleSteps(final String taskId,
                                                           final ProjectContext projectContext,
                                                           final ProjectEntity project) {
        return List.of(
                new AnalyzeGitHistoryStep(
                        taskId,
                        project,
                        authorService,
                        packageService,
                        classService,
                        classContributionService,
                        classLinesOfCodeService,
                        classComplexityService,
                        packageComplexityService,
                        packageLinesOfCodeService,
                        projectFileService
                ),
                new AnalyzeLCOM4MetricStep(
                        taskId,
                        new LCOM4MetricsService(),
                        projectContext,
                        project,
                        packageService,
                        classService,
                        cohesionService
                ),
                new AnalyzeComponentVisibilityStep(
                        taskId,
                        COMPONENT_VISIBILITY_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        packageVisibilityMetricsService
                ),
                new AnalyzeDepthOfInheritanceTreeStep(
                        taskId,
                        DEPTH_OF_INHERITANCE_SERVICE,
                        projectContext,
                        project
                ),
                new AnalyzeCumulativeComponentDependencyStep(
                        taskId,
                        CUMULATIVE_COMPONENT_DEPENDENCY_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        packageCumulativeComponentDependencyMetricsService
                ),
                new AnalyzeRelationalCohesionStep(
                        taskId,
                        RELATIVE_COHESION_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        relationalCohesionMetricsService
                ),
                new AnalyzeComponentDependenciesStep(
                        taskId,
                        COMPONENT_DEPENDENCY_METRICS_SERVICE,
                        projectContext,
                        project,
                        packageService,
                        packageComponentDependencyMetricsService
                )
        );
    }

    /**
     * Generates a task id.
     *
     * @return The generated task id.
     */
    public String generateTaskId() {
        return UUID.randomUUID().toString();
    }
}
