package software.bananen.gavel.ports.usecases;

/**
 * A response model for the schedule project analysis request.
 */
public sealed interface ScheduleProjectAnalysisResponseModel
        permits ScheduleProjectAnalysisResponseModel.Success,
        ScheduleProjectAnalysisResponseModel.Failure {

    /**
     * A successful response.
     */
    record Success() implements ScheduleProjectAnalysisResponseModel {
    }

    /**
     * A failed response.
     *
     * @param errorMessage A error message that describes the cause.
     */
    record Failure(
            String errorMessage) implements ScheduleProjectAnalysisResponseModel {
    }
}
