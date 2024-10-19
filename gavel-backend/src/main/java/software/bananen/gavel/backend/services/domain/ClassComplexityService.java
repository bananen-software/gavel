package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.ComplexityRating;
import software.bananen.gavel.backend.entity.ClassComplexityEntity;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.repository.ClassComplexityRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class ClassComplexityService {

    private final ClassComplexityRepository repository;

    public ClassComplexityService(@Autowired final ClassComplexityRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final ClassContributionEntity classContributionEntity,
                               final Optional<ClassContributionEntity> latestContribution,
                               final Integer complexity) {
        final Integer latestComplexity =
                latestContribution.map(ClassContributionEntity::getClassComplexities)
                        .flatMap(cc -> cc.stream().findFirst())
                        .map(ClassComplexityEntity::getComplexity)
                        .orElse(0);

        final Integer addedComplexity =
                complexity - latestComplexity;

        final ClassComplexityEntity measuredComplexity =
                repository.findByContribution(classContributionEntity).orElse(new ClassComplexityEntity());

        measuredComplexity.setComplexity(complexity);
        measuredComplexity.setContribution(classContributionEntity);
        measuredComplexity.setComplexityRating(
                ComplexityRating.getClassComplexityRating(complexity));
        measuredComplexity.setAddedComplexity(addedComplexity);

        final ClassEntity classEntity = classContributionEntity.getClassField();

        classEntity.setComplexity(complexity);
        classEntity.setComplexityRating(ComplexityRating.getClassComplexityRating(complexity));
        
        classContributionEntity.setClassComplexities(new HashSet<>(List.of(measuredComplexity)));

        repository.save(measuredComplexity);
    }
}
