package software.bananen.gavel.infrastructure.restapi;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import software.bananen.gavel.ports.usecases.*;

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

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("{workspaceId}")
    public void locateProjects(@PathVariable Long workspaceId) {
        final var requestModel = new LocateProjectsInWorkspaceRequest(workspaceId);

        final var responseModel = locateUseCase.execute(requestModel);

        System.out.println(responseModel);
    }
}
