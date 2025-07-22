package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPackageComplexityRepository
        extends JpaRepository<JpaPackageComplexityEntity, Long> {

    Optional<JpaPackageComplexityEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
