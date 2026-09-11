package software.bananen.gavel.infrastructure.restapi;

import io.micrometer.observation.annotation.Observed;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import software.bananen.gavel.ports.usecases.CreateWorkspaceRequestModel;
import software.bananen.gavel.ports.usecases.CreateWorkspaceResponseModel;
import software.bananen.gavel.ports.usecases.CreateWorkspaceUseCase;
import software.bananen.gavel.ports.usecases.LocateProjectsInWorkspaceRequest;
import software.bananen.gavel.ports.usecases.LocateProjectsInWorkspaceUseCase;

import static java.util.Objects.requireNonNull;

/**
 * A controller that can be used to access workspaces.
 */
@RestController
@RequestMapping(value = "/workspaces")
public class WorkspaceController {

    private final CreateWorkspaceUseCase createUseCase;
    private final LocateProjectsInWorkspaceUseCase locateUseCase;

    /**
     * Creates a new instance.
     *
     * @param createUseCase The create workspace use case.
     * @param locateUseCase The locate projects in workspace use case.
     */
    public WorkspaceController(final CreateWorkspaceUseCase createUseCase,
                               final LocateProjectsInWorkspaceUseCase locateUseCase) {
        this.createUseCase =
                requireNonNull(createUseCase, "The create use case may not be null");
        this.locateUseCase =
                requireNonNull(locateUseCase, "The locate use case may not be null");
    }

    /**
     * Creates a new workspace.
     *
     * @param request The request that should be used.
     * @return The id of the created workspace.
     */
    @Observed
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping
    public WorkspaceCreatedResponse create(@RequestBody final CreateWorkspaceRequest request) {
        final var requestModel =
                new CreateWorkspaceRequestModel(
                        request.name(),
                        request.path(),
                        request.excludedPaths(),
                        request.basePackage());

        final var response = createUseCase.createWorkspace(requestModel);

        switch (response) {
            case CreateWorkspaceResponseModel.Failure failure ->
                    throw new ResponseStatusException(HttpStatusCode.valueOf(400),
                            failure.message());
            case CreateWorkspaceResponseModel.Success success -> {
                return new WorkspaceCreatedResponse(success.id());
            }
        }
    }

    @Observed
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("{workspaceId}")
    public void locateProjects(@PathVariable Long workspaceId) {
        final var requestModel = new LocateProjectsInWorkspaceRequest(workspaceId);

        final var responseModel = locateUseCase.execute(requestModel);

        System.out.println(responseModel);
    }
}
