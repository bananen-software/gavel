package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassLinesOfCodeRepository
        extends JpaRepository<ClassLinesOfCodeEntity, Long> {

    Optional<ClassLinesOfCodeEntity> findByContribution(
            final ClassContributionEntity contribution);
}
