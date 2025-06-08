package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/**
 * A repository that can be used to access workspaces.
 */
@Repository
public interface JpaWorkspaceRepository extends JpaRepository<WorkspaceEntity, Long> {

    /**
     * Attempts to find a workspace by its value.
     *
     * @param name The value of the workspace.
     * @return The workspace.
     */
    Optional<WorkspaceEntity> findByName(String name);
}
