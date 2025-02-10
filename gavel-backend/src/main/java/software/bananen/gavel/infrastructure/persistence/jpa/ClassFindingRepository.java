package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ClassFindingRepository extends JpaRepository<ClassFindingEntity, Long> {
    
    Collection<ClassFindingEntity> findByClassFieldId(Long id);
}
