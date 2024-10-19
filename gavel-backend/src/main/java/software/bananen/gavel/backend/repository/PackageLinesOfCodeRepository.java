package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.entity.PackageLinesOfCodeEntity;

import java.util.Optional;

public interface PackageLinesOfCodeRepository
        extends JpaRepository<PackageLinesOfCodeEntity, Long> {

    Optional<PackageLinesOfCodeEntity> findByPackageField(final PackageEntity packageEntity);
}
