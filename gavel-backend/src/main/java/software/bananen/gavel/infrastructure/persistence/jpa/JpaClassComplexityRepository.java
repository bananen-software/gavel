package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaClassComplexityRepository
        extends JpaRepository<JpaClassComplexityEntity, Long> {

    Optional<JpaClassComplexityEntity> findByContribution(final JpaClassContributionEntity contribution);

    List<JpaClassComplexityEntity> findByContributionId(final long id);
}
