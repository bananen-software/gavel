package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaAuthorRepository extends JpaRepository<JpaAuthorEntity, Long> {

    Optional<JpaAuthorEntity> findByNameAndEmail(String name, String email);
}
