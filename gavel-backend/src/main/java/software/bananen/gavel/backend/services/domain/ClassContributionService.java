package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.AuthorEntity;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.repository.ClassContributionRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
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

            Set<ClassContributionEntity> classContributions = classEntity.getClassContributions();
            classContributions.add(contribution);
            classEntity.setClassContributions(classContributions);

            Set<ClassContributionEntity> authorContributions = authorEntity.getClassContributions();
            authorContributions.add(contribution);
            authorEntity.setClassContributions(authorContributions);

            return contribution;
        };
    }

    public Optional<ClassContributionEntity> findLatestContributionTo(final ClassEntity classEntity) {
        return repository.findTopByClassFieldOrderByTimestampDesc(classEntity);
    }
}
