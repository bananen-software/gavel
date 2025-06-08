package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageComplexityRepository
        extends JpaRepository<PackageComplexityEntity, Long> {

    Optional<PackageComplexityEntity> findByPackageField(final PackageEntity packageEntity);
}
