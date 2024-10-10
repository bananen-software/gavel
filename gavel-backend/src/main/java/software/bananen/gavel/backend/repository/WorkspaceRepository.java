package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import software.bananen.gavel.backend.entity.WorkspaceEntity;

@Repository
public interface WorkspaceRepository
        extends JpaRepository<WorkspaceEntity, Long> {
}
