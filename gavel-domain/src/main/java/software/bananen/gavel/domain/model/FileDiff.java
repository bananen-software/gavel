package software.bananen.gavel.domain.model;

import software.bananen.gavel.domain.ports.driven.VersionControlSystemException;

import java.util.Objects;

/**
 * A diff that represents what changes were made to a certain file in a version control system.
 */
public interface FileDiff {

    /**
     * The old path of the file.
     *
     * @return The old path.
     */
    String oldPath();

    /**
     * The new path of the file.
     *
     * @return The new path.
     */
    String newPath();

    /**
     * Loads the content from the file diff.
     *
     * @return The content.
     * @throws VersionControlSystemException Might be thrown in case that the content could not be loaded.
     */
    byte[] loadContent() throws VersionControlSystemException;

    /**
     * The type of the diff.
     *
     * @return The type of the diff.
     */
    DiffType type();

    /**
     * Checks whether the diff has the given type.
     *
     * @param diffType The type of the diff.
     * @return True if the diff has the given type, otherwise false.
     */
    default boolean hasType(DiffType diffType) {
        return Objects.equals(type(), diffType);
    }
}
