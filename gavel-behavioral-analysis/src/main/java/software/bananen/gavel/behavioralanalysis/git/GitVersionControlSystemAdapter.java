package software.bananen.gavel.behavioralanalysis.git;

import software.bananen.gavel.domain.ports.driven.VersionControlRepository;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemPort;

import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * An adapter that implements the {@link VersionControlSystemPort} for the git version control system.
 */
public class GitVersionControlSystemAdapter implements VersionControlSystemPort {

    private static final GitService GIT_SERVICE = new GitService();

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<VersionControlRepository> findRepositoriesIn(final Path path) throws VersionControlSystemException {
        if (!path.toFile().exists()) {
            throw new VersionControlSystemException("The path does not exist");
        }

        if (!path.toFile().isDirectory()) {
            throw new VersionControlSystemException("The path is not a directory");
        }

        return GIT_SERVICE.locateGitRepositories(path).stream()
                .map(GitVersionControlRepository::new)
                .collect(Collectors.toSet());
    }
}
