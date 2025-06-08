package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassComplexityRepository
        extends JpaRepository<ClassComplexityEntity, Long> {

    Optional<ClassComplexityEntity> findByContribution(final ClassContributionEntity contribution);

    List<ClassComplexityEntity> findByContributionId(final long id);
}
