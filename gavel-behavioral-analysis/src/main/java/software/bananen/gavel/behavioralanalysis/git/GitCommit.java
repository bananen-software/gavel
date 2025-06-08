package software.bananen.gavel.behavioralanalysis.git;

import software.bananen.gavel.domain.model.Author;
import software.bananen.gavel.domain.model.Commit;
import software.bananen.gavel.domain.model.FileDiff;

import java.time.LocalDateTime;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

/**
 * An implementation of the {@link Commit} interface that makes use of the git version control system.
 */
public record GitCommit(String identifier,
                        Author author,
                        LocalDateTime timestamp,
                        String shortMessage,
                        String fullMessage,
                        Collection<FileDiff> diffs) implements Commit {

    /**
     * Creates a new instance.
     *
     * @param identifier   The identifier.
     * @param author       The author.
     * @param timestamp    The timestamp.
     * @param shortMessage The short message.
     * @param fullMessage  The full message.
     * @param diffs        The diffs.
     */
    public GitCommit(final String identifier,
                     final Author author,
                     final LocalDateTime timestamp,
                     final String shortMessage,
                     final String fullMessage,
                     final Collection<FileDiff> diffs) {
        this.identifier = requireNonNull(identifier, "The identifier may not be null");
        this.author = requireNonNull(author, "The author may not be null");
        this.timestamp = requireNonNull(timestamp, "The timestamp may not be null");
        this.shortMessage = requireNonNull(shortMessage, "The short message may not be null");
        this.fullMessage = requireNonNull(fullMessage, "The full message may not be null");
        this.diffs = requireNonNull(diffs, "The diffs may not be null");
    }
}
