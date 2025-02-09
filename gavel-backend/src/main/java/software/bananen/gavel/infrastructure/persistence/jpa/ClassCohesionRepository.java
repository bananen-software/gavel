package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassCohesionRepository
        extends JpaRepository<ClassCohesionEntity, Long> {

    Optional<ClassCohesionEntity> findByClassField(final ClassEntity classEntity);
}
