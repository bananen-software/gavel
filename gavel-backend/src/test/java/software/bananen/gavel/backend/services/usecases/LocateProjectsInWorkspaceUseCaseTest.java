package software.bananen.gavel.backend.services.usecases;

import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.*;
import software.bananen.gavel.domain.ports.service.LocateProjectsInWorkspaceService;
import software.bananen.gavel.domain.service.WorkspaceService;
import software.bananen.gavel.ports.usecases.LocateProjectsInWorkspaceRequest;
import software.bananen.gavel.ports.usecases.LocateProjectsInWorkspaceResponseModel;
import software.bananen.gavel.ports.usecases.LocateProjectsInWorkspaceUseCase;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class LocateProjectsInWorkspaceUseCaseTest {

    @Test
    public void execute_requestMayNotBeNull() {
        final var workspaceService = mock(WorkspaceService.class);
        final var locateProjectsService = mock(LocateProjectsInWorkspaceService.class);

        final var useCase =
                new LocateProjectsInWorkspaceUseCase(workspaceService, locateProjectsService);

        final var response =
                useCase.execute(null);

        assertThat(response).isEqualTo(
                new LocateProjectsInWorkspaceResponseModel.Failure("The request may not be null"));
    }

    @Test
    public void execute_workspaceIdMayNotBeNull() {
        final var workspaceService = mock(WorkspaceService.class);
        final var locateProjectsService = mock(LocateProjectsInWorkspaceService.class);

        final var useCase =
                new LocateProjectsInWorkspaceUseCase(workspaceService, locateProjectsService);

        final var response =
                useCase.execute(new LocateProjectsInWorkspaceRequest(null));

        assertThat(response).isEqualTo(
                new LocateProjectsInWorkspaceResponseModel.Failure("The workspace ID may not be null"));
    }

    @Test
    public void execute_workspaceNotFound() {
        final var workspaceService = mock(WorkspaceService.class);
        final var locateProjectsService = mock(LocateProjectsInWorkspaceService.class);

        when(workspaceService.findById(new WorkspaceIdValueObject(1L))).thenReturn(Optional.empty());

        final var useCase =
                new LocateProjectsInWorkspaceUseCase(workspaceService, locateProjectsService);

        final var response =
                useCase.execute(new LocateProjectsInWorkspaceRequest(1L));

        assertThat(response).isEqualTo(
                new LocateProjectsInWorkspaceResponseModel.Failure("The workspace with ID 1 could not be found"));
    }

    @Test
    public void execute_noProjectsFound() {
        final var workspaceService = mock(WorkspaceService.class);
        final var locateProjectsService = mock(LocateProjectsInWorkspaceService.class);

        final var workspace = new WorkspaceAggregate(
                new WorkspaceEntity(
                        new WorkspaceIdValueObject(1L),
                        new WorkspaceNameValueObject("Some workspace"),
                        new WorkspacePathValueObject(Path.of("/tmp/workspace")),
                        Collections.emptyList(),
                        new WorkspaceBasePackageValueObject("com.example")
                ),
                new ArrayList<>()
        );

        when(workspaceService.findById(new WorkspaceIdValueObject(1L))).thenReturn(Optional.of(workspace));
        when(locateProjectsService.locateProjectsInWorkspace(workspace))
                .thenReturn(Collections.emptyList());

        final var useCase =
                new LocateProjectsInWorkspaceUseCase(workspaceService, locateProjectsService);

        final var response =
                useCase.execute(new LocateProjectsInWorkspaceRequest(1L));

        assertThat(response).isEqualTo(
                new LocateProjectsInWorkspaceResponseModel.Failure("No projects found in the workspace with ID 1"));
    }

    @Test
    public void execute_projectsLocatedSuccessfully() {
        final var workspaceService = mock(WorkspaceService.class);
        final var locateProjectsService = mock(LocateProjectsInWorkspaceService.class);

        final var workspace = new WorkspaceAggregate(
                new WorkspaceEntity(
                        new WorkspaceIdValueObject(1L),
                        new WorkspaceNameValueObject("Some workspace"),
                        new WorkspacePathValueObject(Path.of("/tmp/workspace")),
                        Collections.emptyList(),
                        new WorkspaceBasePackageValueObject("com.example")
                ),
                new ArrayList<>()
        );

        final var projectPath = Path.of("/tmp/workspace/coolproject/");

        when(workspaceService.findById(new WorkspaceIdValueObject(1L))).thenReturn(Optional.of(workspace));
        when(locateProjectsService.locateProjectsInWorkspace(workspace))
                .thenReturn(List.of(new LocateProjectsInWorkspaceService.LocatedProject(
                        new ProjectNameValueObject("coolproject"), new ProjectPathValueObject(projectPath))));

        final var useCase =
                new LocateProjectsInWorkspaceUseCase(workspaceService, locateProjectsService);

        final var response =
                useCase.execute(new LocateProjectsInWorkspaceRequest(1L));

        assertThat(response).isEqualTo(
                new LocateProjectsInWorkspaceResponseModel.Success(List.of("coolproject")));

        verify(workspaceService, times(1)).save(new WorkspaceAggregate(
                new WorkspaceEntity(
                        new WorkspaceIdValueObject(1L),
                        new WorkspaceNameValueObject("Some workspace"),
                        new WorkspacePathValueObject(Path.of("/tmp/workspace")),
                        Collections.emptyList(),
                        new WorkspaceBasePackageValueObject("com.example")
                ),
                List.of(new ProjectEntity(
                        null,
                        new ProjectNameValueObject("coolproject"),
                        new ProjectPathValueObject(projectPath),
                        AnalysisStatus.NOT_RUN,
                        null
                ))
        ));
    }
}