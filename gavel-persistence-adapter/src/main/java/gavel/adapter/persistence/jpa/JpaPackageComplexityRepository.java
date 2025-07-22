package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaPackageComplexityRepository
        extends JpaRepository<JpaPackageComplexityEntity, Long> {

    Optional<JpaPackageComplexityEntity> findByPackageField(final JpaPackageEntity packageEntity);
}
