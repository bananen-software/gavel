package software.bananen.gavel.backend.services.usecases;

import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.*;
import software.bananen.gavel.domain.ports.driven.WorkspaceRepository;
import software.bananen.gavel.ports.usecases.CreateWorkspaceRequestModel;
import software.bananen.gavel.ports.usecases.CreateWorkspaceResponseModel;
import software.bananen.gavel.ports.usecases.CreateWorkspaceUseCase;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateWorkspaceUseCaseTest {

    @Test
    public void repositoryMayNotBeNull() {
        try {
            new CreateWorkspaceUseCase(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e.getMessage()).isEqualTo("The repository may not be null");
        }
    }

    @Test
    public void workspaceAlreadyExists() {
        final WorkspaceRepository repository = mock(WorkspaceRepository.class);

        when(repository.findByName(new WorkspaceNameValueObject("example")))
                .thenReturn(Optional.of(new WorkspaceAggregate(new WorkspaceEntity(
                        new WorkspaceIdValueObject(1L),
                        new WorkspaceNameValueObject("example"),
                        new WorkspacePathValueObject(Paths.get("/tmp/")),
                        Collections.emptyList(),
                        new WorkspaceBasePackageValueObject("com.example")
                ), new ArrayList<>())));

        final CreateWorkspaceUseCase useCase = new CreateWorkspaceUseCase(repository);

        final CreateWorkspaceRequestModel request =
                new CreateWorkspaceRequestModel(
                        "example", "/tmp/", Collections.emptyList(), "com.example");

        final CreateWorkspaceResponseModel actual = useCase.createWorkspace(request);

        assertThat(actual)
                .isEqualTo(new CreateWorkspaceResponseModel.Failure("The workspace already exists"));
    }
}