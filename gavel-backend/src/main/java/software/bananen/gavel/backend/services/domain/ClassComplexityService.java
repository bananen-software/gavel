package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.JpaClassComplexityEntity;
import gavel.adapter.persistence.jpa.JpaClassContributionEntity;
import gavel.adapter.persistence.jpa.JpaClassEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.ClassComplexityRating;
import software.bananen.gavel.domain.service.RateClassComplexityService;
import gavel.adapter.persistence.jpa.JpaClassComplexityRepository;

import java.util.Optional;

@Service
public class ClassComplexityService {

    private final JpaClassComplexityRepository repository;

    public ClassComplexityService(@Autowired final JpaClassComplexityRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaClassContributionEntity classContributionEntity,
                               final Optional<JpaClassContributionEntity> latestContribution,
                               final Integer complexity) {
        final Integer latestComplexity =
                latestContribution.map(JpaClassContributionEntity::getClassComplexities)
                        .flatMap(cc -> cc.stream().findFirst())
                        .map(JpaClassComplexityEntity::getComplexity)
                        .orElse(0);

        final Integer addedComplexity =
                complexity - latestComplexity;

        final JpaClassComplexityEntity measuredComplexity =
                repository.findByContribution(classContributionEntity).orElse(new JpaClassComplexityEntity());

        final ClassComplexityRating complexityRating =
                new RateClassComplexityService().rate(complexity);

        measuredComplexity.setComplexity(complexity);
        measuredComplexity.setContribution(classContributionEntity);
        measuredComplexity.setComplexityRating(
                complexityRating);
        measuredComplexity.setAddedComplexity(addedComplexity);

        final JpaClassEntity classEntity = classContributionEntity.getClassField();

        classEntity.setComplexity(complexity);
        classEntity.setComplexityRating(complexityRating);

        classContributionEntity.getClassComplexities().add(measuredComplexity);

        repository.save(measuredComplexity);
    }
}
