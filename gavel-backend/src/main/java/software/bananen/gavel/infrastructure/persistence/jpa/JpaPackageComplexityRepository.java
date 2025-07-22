package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaPackageComplexityEntity;
import gavel.adapter.persistence.jpa.JpaPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPackageComplexityRepository
        extends JpaRepository<JpaPackageComplexityEntity, Long> {

    Optional<JpaPackageComplexityEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
