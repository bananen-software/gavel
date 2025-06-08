package software.bananen.gavel.behavioralanalysis.git;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import software.bananen.gavel.domain.ports.driven.DiffType;
import software.bananen.gavel.domain.ports.driven.FileDiff;
import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;

import java.io.IOException;

import static java.util.Objects.requireNonNull;

/**
 * An implementation of the {@link FileDiff} adapter, that makes use of the git version control system.
 */
public class GitFileDiff implements FileDiff {

    private final DiffEntry diff;
    private final Repository repository;
    private final RevCommit commit;

    /**
     * Creates a new instance.
     *
     * @param diff       The diff.
     * @param repository The repository.
     * @param commit     The commit.
     */
    public GitFileDiff(final DiffEntry diff,
                       final Repository repository,
                       final RevCommit commit) {
        this.diff = requireNonNull(diff, "The diff may not be null");
        this.repository = requireNonNull(repository, "The repository may not be null");
        this.commit = requireNonNull(commit, "The commit may not be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String oldPath() {
        return diff.getOldPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String newPath() {
        return diff.getNewPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] loadContent() throws VersionControlSystemException {
        try {
            return GitUtil.loadRawFileContentFromDiff(repository, commit, diff);
        } catch (final IOException e) {
            throw new VersionControlSystemException("Failed to load content from commit " + commit.name(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DiffType type() {
        return switch (diff.getChangeType()) {
            case ADD -> DiffType.ADDED;
            case MODIFY, COPY -> DiffType.CHANGED;
            case DELETE -> DiffType.DELETED;
            case RENAME -> DiffType.MOVED;
        };
    }
}
