package software.bananen.gavel.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that can be used to track the complexity history of contributions
 * by the authors of the project.
 */
public final class AuthorComplexityHistory {

    private final Map<Author, Long> authorComplexityHistory =
            new ConcurrentHashMap<>();

    private final Map<Author, Long> numberOfChanges = new ConcurrentHashMap<>();

    /**
     * Adds a complexity entry to the history.
     *
     * @param author The author that the complexity history entry should be
     *               added to.
     * @param entry  The entry that should be added for the author.
     */
    public void addComplexity(final Author author,
                              final FileComplexityHistoryEntry entry) {
        authorComplexityHistory.putIfAbsent(author, 0L);
        numberOfChanges.putIfAbsent(author, 0L);

        authorComplexityHistory.put(author,
                authorComplexityHistory.get(author) + entry.complexityDelta());
        numberOfChanges.put(author,
                numberOfChanges.get(author) + 1);
    }

    /**
     * Lists the entries of the history.
     *
     * @return The entries.
     */
    public Collection<AuthorComplexityHistoryEntry> list() {
        final Collection<AuthorComplexityHistoryEntry> result = new ArrayList<>();

        for (final Map.Entry<Author, Long> entry : authorComplexityHistory.entrySet()) {
            final Long numberOfChangesByAuthor = numberOfChanges.get(entry.getKey());

            result.add(new AuthorComplexityHistoryEntry(
                    entry.getKey(),
                    entry.getValue(),
                    numberOfChangesByAuthor,
                    entry.getValue() / numberOfChangesByAuthor.doubleValue()
            ));
        }

        return result;
    }
}
