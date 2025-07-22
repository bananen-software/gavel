package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaClassContributionEntity;
import gavel.adapter.persistence.jpa.JpaClassLinesOfCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaClassLinesOfCodeRepository
        extends JpaRepository<JpaClassLinesOfCodeEntity, Long> {

    Optional<JpaClassLinesOfCodeEntity> findByContribution(
            final JpaClassContributionEntity contribution);
}
