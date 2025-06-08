package software.bananen.gavel.domain.service;

import org.junit.jupiter.api.Test;
import software.bananen.gavel.domain.model.*;
import software.bananen.gavel.domain.ports.repositories.WorkspaceRepository;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkspaceServiceTest {

    @Test
    public void findOrCreateWorkspace_existingWorkspace() {
        final var id = new WorkspaceIdValueObject(1L);
        final var name = new WorkspaceNameValueObject("Cool workspace");
        final var path = new WorkspacePathValueObject(Paths.get("/tmp"));
        final var excludedPaths = Collections.<WorkspaceExcludedPathValueObject>emptyList();
        final var basePackage = new WorkspaceBasePackageValueObject("com.example");

        final var workspace = new WorkspaceAggregate(
                new WorkspaceEntity(id, name, path, excludedPaths, basePackage),
                new ArrayList<>()
        );

        final var repository = mock(WorkspaceRepository.class);

        when(repository.findByName(name)).thenReturn(Optional.of(workspace));

        final var service = new WorkspaceService(repository);

        final var actual =
                service.findOrCreateWorkspace(name, path, excludedPaths, basePackage);

        assertThat(actual).isEqualTo(workspace);
    }

    @Test
    public void findOrCreateWorkspace_nonExistingWorkspace() {
        final var name = new WorkspaceNameValueObject("Cool workspace");
        final var path = new WorkspacePathValueObject(Paths.get("/tmp"));
        final var excludedPaths = Collections.<WorkspaceExcludedPathValueObject>emptyList();
        final var basePackage = new WorkspaceBasePackageValueObject("com.example");

        final var repository = mock(WorkspaceRepository.class);

        when(repository.findByName(name)).thenReturn(Optional.empty());

        final var service = new WorkspaceService(repository);

        final var actual =
                service.findOrCreateWorkspace(name, path, excludedPaths, basePackage);

        assertThat(actual.getAggregateRoot())
                .isEqualTo(new WorkspaceEntity(null, name, path, excludedPaths, basePackage));
    }
}