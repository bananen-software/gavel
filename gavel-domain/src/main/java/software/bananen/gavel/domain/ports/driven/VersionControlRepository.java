package software.bananen.gavel.domain.ports.driven;

import software.bananen.gavel.domain.model.Commit;

import java.util.Collection;

/**
 * An interface for objects that represent a version control repository.
 */
public interface VersionControlRepository {

    /**
     * An extracted name of the project from the repository.
     *
     * @return The name of the project.
     */
    String projectName();

    /**
     * Loads the commits from the repository.
     *
     * @return The commits.
     * @throws VersionControlSystemException Might be thrown in case that loading the commits failed.
     */
    Collection<Commit> commits() throws VersionControlSystemException;

    /**
     * Loads the commits after the given identifier from the repository.
     *
     * @param identifier The identifier.
     * @return The commits.
     * @throws VersionControlSystemException Might be thrown in case that loading the commits failed.
     */
    Collection<Commit> commitsAfter(String identifier) throws VersionControlSystemException;
}
