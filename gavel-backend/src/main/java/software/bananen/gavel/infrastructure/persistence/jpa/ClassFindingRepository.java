package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassFindingRepository extends JpaRepository<ClassFindingEntity, Long> {
}
