package software.bananen.gavel.metrics;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that can be used to track the history of a files' complexity.
 */
public final class FileComplexityHistory {

    private final Map<String, Collection<FileComplexityHistoryEntry>> store =
            new ConcurrentHashMap<>();

    /**
     * Adds a file complexity entry to the history.
     *
     * @param fileComplexity The file complexity.
     * @param timestamp      The timestamp of the commit.
     * @return The entry.
     */
    public FileComplexityHistoryEntry addFileComplexity(final FileComplexity fileComplexity,
                                                        final LocalDateTime timestamp) {
        final Collection<FileComplexityHistoryEntry> fileHistory =
                store.computeIfAbsent(fileComplexity.file(), k -> new ArrayList<>());

        final FileComplexityHistoryEntry previousEntry =
                fileHistory.stream()
                        .skip(fileHistory.isEmpty() ? 0 : fileHistory.size() - 1)
                        .findFirst()
                        .orElse(new FileComplexityHistoryEntry(
                                        LocalDateTime.MIN,
                                        new FileComplexity(
                                                fileComplexity.projectName(),
                                                fileComplexity.file(),
                                                0,
                                                0
                                        ),
                                        0,
                                        0
                                )
                        );

        final long linesOfCodeDelta =
                fileComplexity.linesOfCode() - previousEntry.fileComplexity().linesOfCode();

        final long complexityDelta =
                fileComplexity.complexity() - previousEntry.fileComplexity().complexity();

        final FileComplexityHistoryEntry newEntry = new FileComplexityHistoryEntry(
                timestamp,
                fileComplexity,
                linesOfCodeDelta,
                complexityDelta
        );

        fileHistory.add(newEntry);

        return newEntry;
    }

    /**
     * Lists the code hotspots.
     *
     * @return The code hotspots.
     */
    public Collection<CodeHotspot> listHotspots() {
        return store.entrySet()
                .stream()
                .map(e -> new CodeHotspot(e.getKey(), e.getValue().size()))
                .toList();
    }
}
