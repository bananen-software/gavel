package software.bananen.gavel.backend.services.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.domain.ProjectAggregate;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.WorkspaceEntity;
import software.bananen.gavel.backend.repository.AuthorRepository;
import software.bananen.gavel.backend.repository.ProjectRepository;
import software.bananen.gavel.backend.repository.WorkspaceRepository;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.contextloader.ProjectContextLoader;
import software.bananen.gavel.contextloader.ProjectContextLoaderException;
import software.bananen.gavel.staticanalysis.*;

import java.util.*;

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
    private final AuthorRepository authorRepository;

    public TaskService(@Autowired final WorkspaceRepository workspaceRepository,
                       @Autowired final ProjectRepository projectRepository,
                       @Autowired final AuthorRepository authorRepository) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.authorRepository = authorRepository;
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
                                ws.setName("Cool workspace");
                                return ws;
                            });

            if (workspace.getId() == null) {
                workspaceRepository.save(workspace);
            }

            Set<ProjectEntity> projects =
                    projectRepository.findAllByWorkspace(workspace);

            if (projects.isEmpty()) {
                final ProjectEntity project = new ProjectEntity();
                project.setName("Cool project");
                projects.add(project);
                workspace.setProjects(projects);
                project.setWorkspace(workspace);
                project.setPackages(new HashSet<>());
            }

            projectRepository.saveAll(projects);

            final ProjectEntity project = projects.iterator().next();

            workspace.setProjects(new HashSet<>(Set.of(project)));
            project.setWorkspace(workspace);

            LOGGER.info("[Task: {}] Loading project context", taskId);
            final ProjectContext projectContext = new ProjectContextLoader().loadProjectContext();
            LOGGER.info("[Task: {}] Loaded project context", taskId);

            final ProjectAggregate projectAggregate = new ProjectAggregate(project);

            LOGGER.info("[Task: {}] Starting analysis", taskId);
            assembleSteps(taskId, projectContext, project)
                    .forEach(AbstractAnalysisStep::run);
            LOGGER.info("[Task: {}] Completed analysis", taskId);

            LOGGER.info("[Task: {}] Storing results", taskId);
            workspaceRepository.saveAndFlush(workspace);
            LOGGER.info("[Task: {}] Stored results", taskId);
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
                new AnalyzeJavaClassesStep(
                        taskId,
                        projectContext,
                        project
                ),
                new AnalyzeComponentVisibilityStep(
                        taskId,
                        COMPONENT_VISIBILITY_SERVICE,
                        projectContext,
                        project
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
                        project
                ),
                new AnalyzeRelationalCohesionStep(
                        taskId,
                        RELATIVE_COHESION_SERVICE,
                        projectContext,
                        project
                ),
                new AnalyzeComponentDependenciesStep(
                        taskId,
                        COMPONENT_DEPENDENCY_METRICS_SERVICE,
                        projectContext,
                        project
                ),
                new AnalyzeGitHistoryStep(
                        taskId,
                        project,
                        authorRepository
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
