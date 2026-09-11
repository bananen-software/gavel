package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaProjectFileHistoryRepository extends JpaRepository<JpaFileHistoryEntity, Long> {
}
