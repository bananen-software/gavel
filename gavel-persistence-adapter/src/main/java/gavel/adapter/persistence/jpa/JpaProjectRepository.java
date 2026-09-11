package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import software.bananen.gavel.domain.model.AnalysisStatus;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface JpaProjectRepository extends JpaRepository<JpaProjectEntity, Long> {

    Optional<JpaProjectEntity> findByWorkspaceAndName(JpaWorkspaceEntity workspace, String name);

    Collection<JpaProjectEntity> findByAnalysisStatus(AnalysisStatus status);

    Collection<JpaProjectEntity> findByWorkspaceId(Long id);
}
