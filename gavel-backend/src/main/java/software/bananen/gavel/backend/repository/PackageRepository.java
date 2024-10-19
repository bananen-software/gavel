package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.ProjectEntity;

import java.util.Optional;

public interface PackageRepository extends JpaRepository<PackageEntity, Long> {
    
    Optional<PackageEntity> findByProjectAndPackageName(final ProjectEntity project, String packageName);
}
