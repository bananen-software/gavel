package software.bananen.gavel.domain.ports.driven;

import java.nio.file.Path;
import java.util.Collection;

/**
 * A port that provides access to a version control system.
 */
public interface VersionControlSystemPort {

    /**
     * Scans the given path for VCS repositories.
     *
     * @param path The path top the repositories.
     * @return The found repositories.
     */
    Collection<VersionControlRepository> findRepositoriesIn(Path path) throws VersionControlSystemException;
}
