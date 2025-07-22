package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaClassLinesOfCodeRepository
        extends JpaRepository<JpaClassLinesOfCodeEntity, Long> {

    Optional<JpaClassLinesOfCodeEntity> findByContribution(
            final JpaClassContributionEntity contribution);
}
