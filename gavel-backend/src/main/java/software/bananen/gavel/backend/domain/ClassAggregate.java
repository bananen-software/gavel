package software.bananen.gavel.backend.domain;

import software.bananen.gavel.backend.entity.AuthorEntity;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.entity.ClassEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

public class ClassAggregate implements Aggregate<ClassEntity> {

    private final ClassEntity classEntity;

    public ClassAggregate(final ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassEntity getAggregateRoot() {
        return classEntity;
    }

    public PackageAggregate getPackage() {
        return new PackageAggregate(classEntity.getPackageField());
    }

    public void measureDepthOfInheritanceTree(final Integer value) {
        //TODO: Implement me
    }

    public ClassContributionAggregate findOrCreateContribution(final LocalDateTime timestamp,
                                                               final String vcsIdentifier,
                                                               final AuthorEntity authorEntity) {

        return new ClassContributionAggregate(classEntity.getClassContributions()
                .stream()
                .filter(contribution -> Objects.equals(contribution.getVcsIdentifier(), vcsIdentifier))
                .findFirst()
                .orElseGet(() -> {
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
                }));
    }
}
