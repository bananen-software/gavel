package software.bananen.gavel.ports.usecases;

import software.bananen.gavel.domain.model.ProjectIdValueObject;
import software.bananen.gavel.domain.ports.driven.ProjectRepository;

import static java.util.Objects.requireNonNull;

/**
 * A use case that can be used to schedule an analysis for a specific project.
 */
public class ScheduleProjectAnalysisUseCase {

    private final ProjectRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The repository that should be used.
     */
    public ScheduleProjectAnalysisUseCase(
            final ProjectRepository repository) {
        this.repository = requireNonNull(repository, "The repository may not be null");
    }

    /**
     * Schedules the project analysis.
     *
     * @param request The request that should be executed.
     * @return The response.
     */
    public ScheduleProjectAnalysisResponseModel scheduleProjectAnalysis(final ScheduleProjectAnalysisRequest request) {
        final var projectId = new ProjectIdValueObject(request.projectId());
        final var project = repository.findById(projectId);

        if (project.isPresent()) {
            if (project.get().canScheduleAnalysis()) {
                final var updatedProject = project.get().scheduleAnalysis();

                repository.save(updatedProject);

                return new ScheduleProjectAnalysisResponseModel.Success();
            } else {
                return new ScheduleProjectAnalysisResponseModel.Failure("The project is already being analyzed");
            }
        } else {
            return new ScheduleProjectAnalysisResponseModel.Failure("The project does not exist");
        }
    }
}
