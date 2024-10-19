package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.PackageComplexityEntity;
import software.bananen.gavel.backend.entity.PackageEntity;

import java.util.Optional;

public interface PackageComplexityRepository
        extends JpaRepository<PackageComplexityEntity, Long> {

    Optional<PackageComplexityEntity> findByPackageField(final PackageEntity packageEntity);
}
