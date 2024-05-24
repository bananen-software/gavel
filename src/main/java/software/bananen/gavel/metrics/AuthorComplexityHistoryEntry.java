package software.bananen.gavel.metrics;

/**
 * A record that can be used to represent an entry of an authors complexity
 * history.
 *
 * @param author                  The author of the entry.
 * @param complexityDelta         The complexity delta.
 * @param numberOfChanges         The number of changes.
 * @param relativeComplexityAdded The relative complexity added.
 */
public record AuthorComplexityHistoryEntry(Author author,
                                           Long complexityDelta,
                                           Long numberOfChanges,
                                           Double relativeComplexityAdded) {
}
