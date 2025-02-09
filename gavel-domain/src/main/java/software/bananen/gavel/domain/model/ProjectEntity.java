package software.bananen.gavel.domain.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

/**
 * An entity that represents a project.
 *
 * @param id             The ID of the project.
 * @param name           The name of the project.
 * @param path           The path to the project.
 * @param analysisStatus The analysis status of the project.
 * @param lastAnalyzed   When the project was analyzed for the last time.
 */
public record ProjectEntity(ProjectIdValueObject id,
                            ProjectNameValueObject name,
                            ProjectPathValueObject path,
                            AnalysisStatus analysisStatus,
                            LocalDateTime lastAnalyzed) {

    private static final Collection<AnalysisStatus> PENDING_ANALYSIS_STATUSES =
            Arrays.asList(AnalysisStatus.PENDING, AnalysisStatus.RUNNING);

    /**
     * Checks if an analysis can be scheduled for the project.
     *
     * @return True if an analysis can be scheduled, otherwise false.
     */
    public boolean canScheduleAnalysis() {
        return !PENDING_ANALYSIS_STATUSES.contains(analysisStatus());
    }

    /**
     * Schedules the analysis for the project.
     *
     * @return The updated project.
     */
    public ProjectEntity scheduleAnalysis() {
        return new ProjectEntity(id, name, path, AnalysisStatus.PENDING, lastAnalyzed);
    }

    /**
     * Completes the analysis for the project.
     *
     * @return The updated project.
     */
    public ProjectEntity completeAnalysis() {
        return new ProjectEntity(id, name, path, AnalysisStatus.COMPLETED, LocalDateTime.now());
    }
}
