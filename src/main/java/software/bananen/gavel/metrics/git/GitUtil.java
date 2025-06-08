package software.bananen.gavel.metrics.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import software.bananen.gavel.metrics.Author;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A utility class that helps to extract relevant data from jgit repositories.
 */
public final class GitUtil {

    private static final String DEV_NULL = "/dev/null";

    /**
     * Creates a new instance.
     */
    private GitUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Extracts the author from the given commit.
     *
     * @param commit The commit that the author should be extracted from.
     * @return The extracted author.
     */
    public static Author extractAuthor(final RevCommit commit) {
        return new Author(
                commit.getAuthorIdent().getName(),
                commit.getAuthorIdent().getEmailAddress()
        );
    }

    /**
     * Resolves the object ID from the given commit.
     *
     * @param repository The repository.
     * @param commit     The commit.
     * @return The extracted object ID.
     * @throws IOException Might be thrown in case that an IO operation fails.
     */
    public static ObjectId resolveObjectId(final Repository repository,
                                           final RevCommit commit)
            throws IOException {
        if (commit == null) {
            return null;
        } else {
            return repository.resolve(commit.name());
        }
    }

    /**
     * Extracts the parent commit of the given commit.
     *
     * @param commit The commit.
     * @return The parent commit or null.
     */
    public static RevCommit extractParentCommit(final RevCommit commit) {
        return commit.getParentCount() > 0
                ? commit.getParent(0)
                : null;
    }

    /**
     * Extracts the diff entries from the given repository.
     *
     * @param repository The repository.
     * @param commit     The commit that the diff entries should be extracted from.
     * @return The diff entries.
     * @throws IOException Might be thrown in case that an IO exception occurs.
     */
    public static Collection<DiffEntry> extractDiffEntries(final Repository repository,
                                                           final RevCommit commit) throws IOException {
        try (final DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE)) {
            diffFormatter.setRepository(repository);
            diffFormatter.setDetectRenames(true);

            final RevCommit parentCommit = extractParentCommit(commit);

            return diffFormatter.scan(
                    resolveObjectId(repository, parentCommit),
                    resolveObjectId(repository, commit)
            );
        }
    }

    /**
     * Loads the file content from a specific revision.
     *
     * @param repository The repository.
     * @param commit     The commit.
     * @param diffEntry  The diff entry.
     * @return The loaded file contents.
     * @throws IOException Might be thrown in case that an IO operation failed.
     */
    public static String loadFileContentFromRevision(final Repository repository,
                                                     final RevCommit commit,
                                                     final DiffEntry diffEntry)
            throws IOException {
        if (DEV_NULL.equals(diffEntry.getNewPath()) ||
                DiffEntry.ChangeType.DELETE.equals(diffEntry.getChangeType())) {
            // The file has been deleted therefor the content will be empty
            return "";
        }

        final RevTree tree = commit.getTree();

        try (final TreeWalk treeWalk = new TreeWalk(repository)) {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true); // Not entirely sure why this has such a huge impact
            treeWalk.setFilter(PathFilter.create(diffEntry.getNewPath()));

            if (!treeWalk.next()) {
                // The file has probably been deleted therefor the content will be empty
                return "";
            }

            try {
                final ObjectId objectId = treeWalk.getObjectId(0);
                final ObjectLoader loader = repository.open(objectId);

                return new String(loader.getBytes(), StandardCharsets.UTF_8);
            } catch (final MissingObjectException e) {
                //TODO: Find out why this occurs sometimes
                throw new IOException(
                        String.format("Failed to load file content from revision %s",
                                commit.getId()),
                        e);
            }
        }
    }

    /**
     * Calculates the complexity of a line based on the leading whitespaces.
     * <p>
     * This is a technique derived from the books of Adam Tornhill.
     *
     * @param line The line.
     * @return The number of leading whitespaces for the line.
     */
    public static int calculateWhitespaceComplexity(final String line) {
        int leadingSpaces = 0;

        for (final char c : line.toCharArray()) {
            if (c == ' ') {
                leadingSpaces++;
            } else {
                break;
            }
        }

        return leadingSpaces;
    }

    public static Collection<RevCommit> getCommitsFromOldToNew(final Git git) throws IOException, GitAPIException {
        final List<RevCommit> commits = new ArrayList<>();

        for (final RevCommit commit : git.log().all().call()) {
            commits.add(commit);
        }

        Collections.reverse(commits);

        return commits;
    }

    /**
     * Extracts the timestamp from the given commit.
     *
     * @param commit The commit.
     * @return The timestamp.
     */
    public static LocalDateTime extractTimestampFrom(final RevCommit commit) {
        return LocalDateTime.ofEpochSecond(commit.getCommitTime(), 0, ZoneOffset.UTC);
    }

    /**
     * Loads the contents of a .mailmap file if that exists. If it does not
     * exist, then an empty mailmap will be initialized.
     *
     * @param path The path the mailmap file should be loaded from. This should
     *             point to the root of a git repository.
     * @return The loaded mailmap.
     * @throws IOException Might be thrown in case that loading the mailmap failed.
     */
    public static Mailmap loadMailmap(Path path) throws IOException {
        final Mailmap mailmap = new Mailmap();

        final Path mailmapFile = path.resolve(".mailmap");

        if (mailmapFile.toFile().exists()) {
            Files.readAllLines(mailmapFile).forEach(mailmap::parse);
        }
        return mailmap;
    }

    /**
     * Checks whether the given commit is between the two timestamps.
     *
     * @param commit         The commit.
     * @param startTimestamp The start timestamp.
     * @param endTimestamp   The end timestamp.
     * @return True if the commit is between the two timestamps, otherwise
     * false.
     */
    public static boolean between(final RevCommit commit,
                                  final LocalDateTime startTimestamp,
                                  final LocalDateTime endTimestamp) {
        final LocalDateTime timestamp = GitUtil.extractTimestampFrom(commit);

        return timestamp.isAfter(startTimestamp) && timestamp.isBefore(endTimestamp);
    }
}
