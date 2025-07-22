package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAuthorRepository extends JpaRepository<JpaAuthorEntity, Long> {

    Optional<JpaAuthorEntity> findByNameAndEmail(String name, String email);
}
