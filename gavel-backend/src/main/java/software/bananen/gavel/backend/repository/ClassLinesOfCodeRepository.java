package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ClassContributionEntity;
import software.bananen.gavel.backend.entity.ClassLinesOfCodeEntity;

import java.util.Optional;

public interface ClassLinesOfCodeRepository
        extends JpaRepository<ClassLinesOfCodeEntity, Long> {

    Optional<ClassLinesOfCodeEntity> findByContribution(
            final ClassContributionEntity contribution);
}
