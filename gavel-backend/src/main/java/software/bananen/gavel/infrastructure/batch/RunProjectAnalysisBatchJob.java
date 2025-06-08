package software.bananen.gavel.infrastructure.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import software.bananen.gavel.backend.services.analysis.AnalysisTaskFactory;
import software.bananen.gavel.backend.services.domain.ProjectService;
import software.bananen.gavel.contextloader.ProjectContext;
import software.bananen.gavel.contextloader.ProjectContextData;
import software.bananen.gavel.contextloader.ProjectContextLoader;
import software.bananen.gavel.contextloader.ProjectContextLoaderException;
import software.bananen.gavel.domain.model.AnalysisStatus;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;

import java.time.LocalDateTime;

@Component
public class RunProjectAnalysisBatchJob implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunProjectAnalysisBatchJob.class);

    private final ProjectService projectService;
    private final AnalysisTaskFactory analysisTaskFactory;
    private final ProjectContextLoader projectContextLoader;

    public RunProjectAnalysisBatchJob(@Autowired ProjectService projectService,
                                      @Autowired AnalysisTaskFactory analysisTaskFactory,
                                      @Autowired ProjectContextLoader projectContextLoader) {
        this.projectService = projectService;
        this.analysisTaskFactory = analysisTaskFactory;
        this.projectContextLoader = projectContextLoader;
    }

    @Scheduled(fixedRate = 5000)
    @Override
    @Transactional
    public void run() {
        LOGGER.debug("Starting background task");

        for (final ProjectEntity project :
                projectService.findProjectsPendingForAnalysis()) {
            try {
                LOGGER.info("Starting analysis for project {}[{}]",
                        project.getName(),
                        project.getId());

                final var workspace = project.getWorkspace();

                final var projectContextData =
                        new ProjectContextData(
                                project.getPath(),
                                workspace.getExcludedPath(),
                                workspace.getBasePackage());

                final ProjectContext projectContext =
                        projectContextLoader.loadProjectContext(projectContextData);

                analysisTaskFactory.assembleSteps(projectContext, project)
                        .forEach(Runnable::run);

                project.setAnalysisStatus(AnalysisStatus.COMPLETED);
                project.setLastAnalyzed(LocalDateTime.now());

                projectService.save(project);

                LOGGER.info("Completed analysis for project {}[{}]",
                        project.getName(),
                        project.getId());
            } catch (final ProjectContextLoaderException e) {
                LOGGER.error("Failed to load project context", e);
            }
        }

        LOGGER.debug("Completed background task");
    }
}
