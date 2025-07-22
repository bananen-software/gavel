package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ClassContributionService {

    private final JpaClassContributionRepository repository;
    private final JpaClassRepository classRepository;

    public ClassContributionService(@Autowired final JpaClassContributionRepository repository,
                                    @Autowired final JpaClassRepository classRepository) {
        this.repository = repository;
        this.classRepository = classRepository;
    }

    public JpaClassContributionEntity findOrCreate(JpaClassEntity classEntity,
                                                   LocalDateTime timestamp,
                                                   String commitHash,
                                                   JpaAuthorEntity authorEntity) {

        final Optional<JpaClassContributionEntity> matchingContribution =
                repository.findByClassFieldAndTimestampAndVcsIdentifierAndAuthor(classEntity, timestamp, commitHash, authorEntity);

        final var contribution = matchingContribution.orElseGet(
                () -> repository.save(mapToEntity(classEntity, timestamp, commitHash, authorEntity).get()));

        if (classEntity.getNumberOfChanges() == 0) {
            classEntity.setCreated(timestamp);
        }

        classEntity.setLastModified(timestamp);
        classEntity.setNumberOfChanges(classEntity.getNumberOfChanges() + 1);
        classEntity.setNumberOfAuthors(classEntity.getClassContributions()
                .stream()
                .map(JpaClassContributionEntity::getAuthor)
                .collect(Collectors.toSet())
                .size());

        classRepository.save(classEntity);

        return contribution;
    }

    private Supplier<JpaClassContributionEntity> mapToEntity(final JpaClassEntity classEntity,
                                                             final LocalDateTime timestamp,
                                                             final String vcsIdentifier,
                                                             final JpaAuthorEntity authorEntity) {
        return () -> {
            final JpaClassContributionEntity contribution = new JpaClassContributionEntity();

            contribution.setVcsIdentifier(vcsIdentifier);
            contribution.setTimestamp(timestamp);
            contribution.setAuthor(authorEntity);
            contribution.setClassField(classEntity);

            classEntity.getClassContributions().add(contribution);
            authorEntity.getClassContributions().add(contribution);

            return contribution;
        };
    }

    public Optional<JpaClassContributionEntity> findLatestContributionTo(final JpaClassEntity classEntity) {
        return repository.findTopByClassFieldOrderByTimestampDesc(classEntity);
    }
}
