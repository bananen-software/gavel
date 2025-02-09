package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import software.bananen.gavel.domain.model.AnalysisStatus;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface JpaProjectRepository extends JpaRepository<ProjectEntity, Long> {

    Optional<ProjectEntity> findByWorkspaceAndName(WorkspaceEntity workspace, String name);

    Collection<ProjectEntity> findByAnalysisStatus(AnalysisStatus status);
}
