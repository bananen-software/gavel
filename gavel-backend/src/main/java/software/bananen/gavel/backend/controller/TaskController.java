package software.bananen.gavel.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import software.bananen.gavel.backend.services.analysis.TaskService;
import software.bananen.gavel.backend.tasks.TaskResponse;

import static java.util.Objects.requireNonNull;


@Controller
@RequestMapping(value = "/tasks")
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a new instance.
     *
     * @param taskService The task service that should be used by the controller.
     */
    public TaskController(@Autowired final TaskService taskService) {
        this.taskService = requireNonNull(taskService, "The task service may not be null");
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public ResponseEntity<TaskResponse> scheduleAnalysis(UriComponentsBuilder uriBuilder) {
        final String taskId = taskService.generateTaskId();
        final UriComponents progressURL =
                uriBuilder.path("/tasks/{id}/progress").buildAndExpand(taskId);

        taskService.runAnalysis(taskId);

        return ResponseEntity.accepted().location(progressURL.toUri()).build();
    }
}
