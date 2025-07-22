package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaClassEntity;
import gavel.adapter.persistence.jpa.JpaPackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaClassRepository
        extends JpaRepository<JpaClassEntity, Long> {

    Optional<JpaClassEntity> findByPackageFieldAndName(final JpaPackageEntity packageEntity,
                                                       final String name);

    List<JpaClassEntity> findByPackageFieldId(long packageId);
}
