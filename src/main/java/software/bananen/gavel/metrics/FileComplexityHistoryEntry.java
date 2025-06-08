package software.bananen.gavel.metrics;

import java.time.LocalDateTime;

/**
 * A record that can be used to represent an entry in a files' complexity
 * history.
 *
 * @param timestamp        The timestamp of the entry.
 * @param fileComplexity   The file complexity.
 * @param linesOfCodeDelta The lines of code delta.
 * @param complexityDelta  The complexity delta.
 */
public record FileComplexityHistoryEntry(LocalDateTime timestamp,
                                         FileComplexity fileComplexity,
                                         long linesOfCodeDelta,
                                         long complexityDelta) {
}
