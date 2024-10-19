package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ClassComplexityEntity;
import software.bananen.gavel.backend.entity.ClassContributionEntity;

import java.util.Optional;

public interface ClassComplexityRepository
        extends JpaRepository<ClassComplexityEntity, Long> {

    Optional<ClassComplexityEntity> findByContribution(final ClassContributionEntity contribution);
}
