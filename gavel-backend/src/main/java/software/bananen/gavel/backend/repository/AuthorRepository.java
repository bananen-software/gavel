package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.AuthorEntity;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {

    Optional<AuthorEntity> findByNameAndEmail(String name, String email);
}
