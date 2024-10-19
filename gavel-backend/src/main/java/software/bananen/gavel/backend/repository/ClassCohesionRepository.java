package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ClassCohesionEntity;
import software.bananen.gavel.backend.entity.ClassEntity;

import java.util.Optional;

public interface ClassCohesionRepository
        extends JpaRepository<ClassCohesionEntity, Long> {

    Optional<ClassCohesionEntity> findByClassField(final ClassEntity classEntity);
}
