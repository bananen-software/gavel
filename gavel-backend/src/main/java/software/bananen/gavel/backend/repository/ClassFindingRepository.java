package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ClassFindingEntity;

public interface ClassFindingRepository extends JpaRepository<ClassFindingEntity, Long> {
}
