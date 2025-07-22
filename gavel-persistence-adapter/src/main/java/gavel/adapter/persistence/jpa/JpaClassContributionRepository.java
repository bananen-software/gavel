package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface JpaClassContributionRepository
        extends JpaRepository<JpaClassContributionEntity, Long> {

    Optional<JpaClassContributionEntity> findByClassFieldAndTimestampAndVcsIdentifierAndAuthor(
            JpaClassEntity classField,
            LocalDateTime timestamp,
            String vcsIdentifier,
            JpaAuthorEntity author
    );

    Optional<JpaClassContributionEntity> findTopByClassFieldOrderByTimestampDesc(JpaClassEntity classField);

    Collection<JpaClassContributionEntity> findByClassFieldId(long id);
}
