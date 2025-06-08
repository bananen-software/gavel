package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PackageRepository extends JpaRepository<PackageEntity, Long> {

    Optional<PackageEntity> findByProjectAndPackageName(final ProjectEntity project, String packageName);
    
    List<PackageEntity> findByProjectId(long projectId);
}
