package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

public interface ClassContributionRepository
        extends JpaRepository<ClassContributionEntity, Long> {

    Optional<ClassContributionEntity> findByClassFieldAndTimestampAndVcsIdentifierAndAuthor(
            ClassEntity classField,
            LocalDateTime timestamp,
            String vcsIdentifier,
            AuthorEntity author
    );

    Optional<ClassContributionEntity> findTopByClassFieldOrderByTimestampDesc(ClassEntity classField);

    Collection<ClassContributionEntity> findByClassFieldId(long id);
}
