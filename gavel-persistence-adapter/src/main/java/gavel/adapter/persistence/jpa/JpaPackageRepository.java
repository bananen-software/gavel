package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPackageRepository extends JpaRepository<JpaPackageEntity, Long> {

    Optional<JpaPackageEntity> findByProjectAndPackageName(final JpaProjectEntity project, String packageName);

    List<JpaPackageEntity> findByProjectId(long projectId);
}
