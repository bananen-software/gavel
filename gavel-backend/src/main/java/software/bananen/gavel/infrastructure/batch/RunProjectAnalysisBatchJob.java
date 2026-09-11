package software.bananen.gavel.infrastructure.batch;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaProjectEntity;
import io.micrometer.observation.annotation.Observed;
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

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

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

    @Observed
    @Scheduled(fixedRate = 5000)
    @Override
    @Transactional
    public void run() {
        LOGGER.debug("Starting background task");

        for (final JpaProjectEntity project : projectService.findProjectsPendingForAnalysis()) {
            try {
                LOGGER.info("Starting analysis for project {}[{}]",
                        project.getName(),
                        project.getId());

                try {
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

                    final Set<JpaPackageEntity> packages = project.getPackages();

                    project.setNumberOfTypes(0);
                    project.setTotalLinesOfCode(0);
                    project.setTotalLinesOfComments(0);
                    project.setNumberOfFindings(0);
                    project.setNumberOfHighPriorityFindings(0);
                    project.setNumberOfPackages(packages.size());

                    for (final JpaPackageEntity aPackage : packages) {
                        project.setNumberOfTypes(
                                project.getNumberOfFindings() + aPackage.getClasses().size());
                        project.setTotalLinesOfCode(
                                project.getTotalLinesOfCode() + aPackage.getLinesOfCode());
                        project.setTotalLinesOfComments(
                                project.getTotalLinesOfComments() + aPackage.getLinesOfComments());
                        project.setNumberOfFindings(
                                project.getNumberOfFindings() + aPackage.getTotalNumberOfFindings());
                        project.setNumberOfHighPriorityFindings(
                                project.getNumberOfHighPriorityFindings() + aPackage.getNumberOfHighPriorityFindings());
                    }

                    project.setDefectDensity(
                            ((double) project.getNumberOfFindings() * 1000) / project.getTotalLinesOfCode());
                    project.setHighDefectDensity(
                            ((double) project.getNumberOfHighPriorityFindings() * 1000) / project.getTotalLinesOfCode());
                    project.setCommentToCodeRatio(
                            ((double) project.getTotalLinesOfComments()) / project.getTotalLinesOfCode());

                    project.setAnalysisStatus(AnalysisStatus.COMPLETED);
                    project.setLastAnalyzed(LocalDateTime.now());
                    project.setRepeatedAnalysisFailureCount(0);
                } catch (final Throwable e) {
                    //TODO: Maybe make this catch block a little less broad...
                    project.setRepeatedAnalysisFailureCount(
                            Optional.ofNullable(project.getRepeatedAnalysisFailureCount())
                                    .orElse(0) + 1);

                    if (project.getRepeatedAnalysisFailureCount() >= 5) {
                        project.setAnalysisStatus(AnalysisStatus.FAILED);
                    }

                    if (e instanceof ProjectContextLoaderException) {
                        throw e;
                    }
                }

                projectService.save(project);

                LOGGER.info("Completed analysis for project {}[{}]: {}",
                        project.getName(),
                        project.getId(),
                        project.getAnalysisStatus());
            } catch (final ProjectContextLoaderException e) {
                LOGGER.error("Failed to load project context", e);
            }
        }

        LOGGER.debug("Completed background task");
    }
}
