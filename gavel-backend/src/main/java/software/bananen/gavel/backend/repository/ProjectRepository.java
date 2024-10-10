package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import software.bananen.gavel.backend.entity.ProjectEntity;
import software.bananen.gavel.backend.entity.WorkspaceEntity;

import java.util.Set;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Set<ProjectEntity> findAllByWorkspace(WorkspaceEntity workspace);
}
