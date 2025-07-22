package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface JpaClassFindingRepository extends JpaRepository<JpaClassFindingEntity, Long> {

    Collection<JpaClassFindingEntity> findByClassFieldId(Long id);
}
