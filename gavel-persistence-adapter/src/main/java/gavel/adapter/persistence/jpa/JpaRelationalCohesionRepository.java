package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRelationalCohesionRepository
        extends JpaRepository<JpaRelationalCohesionMetricEntity, Long> {

    Optional<JpaRelationalCohesionMetricEntity> findByPackageFieldId(long packageId);
}
