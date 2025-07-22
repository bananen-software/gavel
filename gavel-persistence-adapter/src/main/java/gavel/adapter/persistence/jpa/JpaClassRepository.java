package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaClassRepository
        extends JpaRepository<JpaClassEntity, Long> {

    Optional<JpaClassEntity> findByPackageFieldAndName(final JpaPackageEntity packageEntity,
                                                       final String name);

    List<JpaClassEntity> findByPackageFieldId(long packageId);
}
