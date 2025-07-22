package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.Author;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaAuthorEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaAuthorRepository;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A service that can be used to interact with authors.
 */
@Service
public class AuthorService {

    private final JpaAuthorRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The repository that should be used by this instance.
     */
    public AuthorService(@Autowired JpaAuthorRepository repository) {
        this.repository =
                requireNonNull(repository, "The repository may not be null");
    }

    /**
     * Finds an existing author matching the value and email or creates one if
     * it does not exist.
     *
     * @param author The authors' data.
     * @return The created author.
     */
    public JpaAuthorEntity findOrCreate(final Author author) {
        final Optional<JpaAuthorEntity> matchingAuthor =
                repository.findByNameAndEmail(author.name(), author.email());

        return matchingAuthor.orElseGet(() ->
                repository.save(mapToEntity(author).get()));
    }

    /**
     * Maps the given author to its entity representation.
     *
     * @param author The author that should be mapped.
     * @return The supplier.
     */
    private static Supplier<JpaAuthorEntity> mapToEntity(final Author author) {
        return () -> {
            final JpaAuthorEntity authorEntity = new JpaAuthorEntity();

            authorEntity.setName(author.name());
            authorEntity.setEmail(author.email());

            return authorEntity;
        };
    }
}
