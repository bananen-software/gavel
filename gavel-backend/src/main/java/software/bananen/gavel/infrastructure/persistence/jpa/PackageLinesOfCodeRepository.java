package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageLinesOfCodeRepository
        extends JpaRepository<PackageLinesOfCodeEntity, Long> {

    Optional<PackageLinesOfCodeEntity> findByPackageField(final PackageEntity packageEntity);
}
