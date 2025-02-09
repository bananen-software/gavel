package software.bananen.gavel.domain.model;

/**
 * An aggregate that represents a specific project.
 *
 * @param aggregateRoot The aggregate root.
 */
public record ProjectAggregate(ProjectEntity aggregateRoot) {

    /**
     * Checks if an analysis can be scheduled for the project.
     *
     * @return True if an analysis can be scheduled, otherwise false.
     */
    public boolean canScheduleAnalysis() {
        return aggregateRoot.canScheduleAnalysis();
    }

    /**
     * Schedules the analysis for the project.
     *
     * @return The updated project.
     */
    public ProjectAggregate scheduleAnalysis() {
        return new ProjectAggregate(aggregateRoot.scheduleAnalysis());
    }

    /**
     * Completes the analysis for the project.
     *
     * @return The updated project.
     */
    public ProjectAggregate completeAnalysis() {
        return new ProjectAggregate(aggregateRoot.completeAnalysis());
    }
}
