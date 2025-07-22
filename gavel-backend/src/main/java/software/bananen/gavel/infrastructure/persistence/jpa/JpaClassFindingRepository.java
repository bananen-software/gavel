package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface JpaClassFindingRepository extends JpaRepository<JpaClassFindingEntity, Long> {

    Collection<JpaClassFindingEntity> findByClassFieldId(Long id);
}
