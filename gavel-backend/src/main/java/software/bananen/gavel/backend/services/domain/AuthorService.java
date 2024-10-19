package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.AuthorEntity;
import software.bananen.gavel.backend.repository.AuthorRepository;
import software.bananen.gavel.behavioralanalysis.Author;

import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * A service that can be used to interact with authors.
 */
@Service
public class AuthorService {

    private final AuthorRepository repository;

    /**
     * Creates a new instance.
     *
     * @param repository The repository that should be used by this instance.
     */
    public AuthorService(@Autowired AuthorRepository repository) {
        this.repository =
                requireNonNull(repository, "The repository may not be null");
    }

    /**
     * Finds an existing author matching the name and email or creates one if
     * it does not exist.
     *
     * @param author The authors' data.
     * @return The created author.
     */
    public AuthorEntity findOrCreate(final Author author) {
        final Optional<AuthorEntity> matchingAuthor =
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
    private static Supplier<AuthorEntity> mapToEntity(final Author author) {
        return () -> {
            final AuthorEntity authorEntity = new AuthorEntity();

            authorEntity.setName(author.name());
            authorEntity.setEmail(author.email());

            return authorEntity;
        };
    }
}
