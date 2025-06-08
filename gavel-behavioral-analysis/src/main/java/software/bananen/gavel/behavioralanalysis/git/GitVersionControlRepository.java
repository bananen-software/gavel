package software.bananen.gavel.behavioralanalysis.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import software.bananen.gavel.domain.ports.driven.Commit;
import software.bananen.gavel.domain.ports.driven.VersionControlRepository;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * An implementation of the {@link VersionControlRepository} interface for the git version control system.
 */
public class GitVersionControlRepository implements VersionControlRepository {

    private static final GitService GIT_SERVICE = new GitService();

    private final Path repositoryPath;

    /**
     * Creates a new instance.
     *
     * @param repositoryPath The path to the repository.
     */
    public GitVersionControlRepository(final Path repositoryPath) {
        this.repositoryPath = requireNonNull(repositoryPath, "The repository path may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String projectName() {
        return extractProjectName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Commit> commits() throws VersionControlSystemException {
        try (final Repository repository = GIT_SERVICE.loadRepository(repositoryPath)) {
            try (final Git git = new Git(repository)) {
                final Mailmap mailmap = loadMailmap();

                final Collection<Commit> commits = new ArrayList<>();

                for (final RevCommit revCommit : GitUtil.getCommitsFromOldToNew(git)) {
                    commits.add(new GitCommit(
                            revCommit.name(),
                            mailmap.map(GitUtil.extractAuthor(revCommit)),
                            GitUtil.extractTimestampFrom(revCommit),
                            revCommit.getShortMessage(),
                            revCommit.getFullMessage(),
                            GitUtil.extractDiffEntries(repository, revCommit)
                                    .stream()
                                    .map(diff -> new GitFileDiff(diff, repository, revCommit))
                                    .collect(Collectors.toList())
                    ));
                }

                return commits;
            }
        } catch (final IOException | GitAPIException e) {
            throw new VersionControlSystemException("Failed to fetch commits", e);
        }
    }

    /**
     * Extracts the project name from the repository path.
     *
     * @return The extracted project name.
     */
    private String extractProjectName() {
        return repositoryPath.getName(repositoryPath.getNameCount() - 1).toString();
    }

    /**
     * Loads the mailmap file from the repository.
     *
     * @return The mailmap file.
     * @throws IOException Might be thrown in case that the mailmap file could not be loaded.
     */
    private Mailmap loadMailmap() throws IOException {
        return GitUtil.loadMailmap(repositoryPath);
    }
}
