package software.bananen.gavel.domain.model;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * An interface for objects that represent a commit in a version control system.
 */
public interface Commit {

    /**
     * An identifier e.g. a hash.
     *
     * @return The identifier.
     */
    String identifier();

    /**
     * A short message that describes the commit.
     *
     * @return The message.
     */
    String shortMessage();

    /**
     * A full message that describes the commit.
     *
     * @return The message.
     */
    String fullMessage();

    /**
     * The author of the commit.
     *
     * @return The author.
     */
    Author author();

    /**
     * The timestamp of the commit.
     *
     * @return The timestamp.
     */
    LocalDateTime timestamp();

    /**
     * Retrieves the diffs of the commit.
     *
     * @return The diffs.
     */
    Collection<FileDiff> diffs();
}
