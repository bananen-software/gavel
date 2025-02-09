package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.ClassComplexityRating;
import software.bananen.gavel.domain.service.RateClassComplexityService;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassComplexityEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassComplexityRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassContributionEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassEntity;

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

        final ClassComplexityRating complexityRating =
                new RateClassComplexityService().rate(complexity);

        measuredComplexity.setComplexity(complexity);
        measuredComplexity.setContribution(classContributionEntity);
        measuredComplexity.setComplexityRating(
                complexityRating);
        measuredComplexity.setAddedComplexity(addedComplexity);

        final ClassEntity classEntity = classContributionEntity.getClassField();

        classEntity.setComplexity(complexity);
        classEntity.setComplexityRating(complexityRating);

        classContributionEntity.getClassComplexities().add(measuredComplexity);

        repository.save(measuredComplexity);
    }
}
