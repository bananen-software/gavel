package software.bananen.gavel.infrastructure.restapi;

import io.micrometer.observation.annotation.Observed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import software.bananen.gavel.ports.usecases.ScheduleProjectAnalysisRequest;
import software.bananen.gavel.ports.usecases.ScheduleProjectAnalysisResponseModel;
import software.bananen.gavel.ports.usecases.ScheduleProjectAnalysisUseCase;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping(value = "/projects")
public class ProjectController {

    private final ScheduleProjectAnalysisUseCase scheduleAnalysisUseCase;

    public ProjectController(@Autowired final ScheduleProjectAnalysisUseCase scheduleAnalysisUseCase) {
        this.scheduleAnalysisUseCase =
                requireNonNull(scheduleAnalysisUseCase, "The schedule analysis use case may not be null");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("{projectId}")
    @Observed
    public void scheduleAnalysis(@PathVariable Long projectId) {
        final var response =
                scheduleAnalysisUseCase.scheduleProjectAnalysis(new ScheduleProjectAnalysisRequest(projectId));

        switch (response) {
            case ScheduleProjectAnalysisResponseModel.Failure failure ->
                    throw new ResponseStatusException(400, failure.errorMessage(), null);

            case ScheduleProjectAnalysisResponseModel.Success success -> {
            }
        }
    }
}
