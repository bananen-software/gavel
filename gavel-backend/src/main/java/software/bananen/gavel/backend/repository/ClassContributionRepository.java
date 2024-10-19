package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.AuthorEntity;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.entity.ClassEntity;

import java.time.LocalDateTime;
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
}
