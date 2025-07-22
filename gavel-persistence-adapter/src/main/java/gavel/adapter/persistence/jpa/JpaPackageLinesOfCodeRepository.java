package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPackageLinesOfCodeRepository
        extends JpaRepository<JpaPackageLinesOfCodeEntity, Long> {

    Optional<JpaPackageLinesOfCodeEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
