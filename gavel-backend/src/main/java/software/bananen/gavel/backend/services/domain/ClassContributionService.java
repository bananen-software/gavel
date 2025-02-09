package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.AuthorEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassContributionEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassContributionRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class ClassContributionService {

    private final ClassContributionRepository repository;

    public ClassContributionService(@Autowired final ClassContributionRepository repository) {
        this.repository = repository;
    }

    public ClassContributionEntity findOrCreate(ClassEntity classEntity,
                                                LocalDateTime timestamp,
                                                String commitHash,
                                                AuthorEntity authorEntity) {

        final Optional<ClassContributionEntity> matchingContribution =
                repository.findByClassFieldAndTimestampAndVcsIdentifierAndAuthor(classEntity, timestamp, commitHash, authorEntity);

        classEntity.setLastModified(timestamp);
        classEntity.setNumberOfChanges(classEntity.getNumberOfChanges() + 1);
        classEntity.setNumberOfAuthors(classEntity.getClassContributions()
                .stream()
                .map(ClassContributionEntity::getAuthor)
                .collect(Collectors.toSet())
                .size());

        return matchingContribution.orElseGet(
                () -> repository.save(mapToEntity(classEntity, timestamp, commitHash, authorEntity).get()));
    }

    private Supplier<ClassContributionEntity> mapToEntity(ClassEntity classEntity,
                                                          LocalDateTime timestamp,
                                                          String vcsIdentifier,
                                                          AuthorEntity authorEntity) {
        return () -> {
            final ClassContributionEntity contribution = new ClassContributionEntity();

            contribution.setVcsIdentifier(vcsIdentifier);
            contribution.setTimestamp(timestamp);
            contribution.setAuthor(authorEntity);
            contribution.setClassField(classEntity);

            classEntity.getClassContributions().add(contribution);
            authorEntity.getClassContributions().add(contribution);

            return contribution;
        };
    }

    public Optional<ClassContributionEntity> findLatestContributionTo(final ClassEntity classEntity) {
        return repository.findTopByClassFieldOrderByTimestampDesc(classEntity);
    }
}
