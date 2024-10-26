package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ChangeCouplingEntity;
import software.bananen.gavel.backend.entity.ClassEntity;

import java.util.Optional;

public interface ChangeCouplingRepository
        extends JpaRepository<ChangeCouplingEntity, Long> {

    Optional<ChangeCouplingEntity> findBySourceClassAndTargetClass(
            ClassEntity sourceClass, ClassEntity targetClass);
}
