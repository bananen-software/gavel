package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaPackageEntity;
import gavel.adapter.persistence.jpa.JpaPackageLinesOfCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPackageLinesOfCodeRepository
        extends JpaRepository<JpaPackageLinesOfCodeEntity, Long> {

    Optional<JpaPackageLinesOfCodeEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
