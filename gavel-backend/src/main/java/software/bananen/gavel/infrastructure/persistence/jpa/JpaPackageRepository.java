package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaPackageRepository extends JpaRepository<JpaPackageEntity, Long> {

    Optional<JpaPackageEntity> findByProjectAndPackageName(final JpaProjectEntity project, String packageName);

    List<JpaPackageEntity> findByProjectId(long projectId);
}
