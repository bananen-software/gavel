package software.bananen.gavel.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A store that can be used to track the change coupling between two files.
 */
public final class ChangeCouplingStore {

    private final Map<ChangeCouplingInstance, Integer> couplingStore =
            new ConcurrentHashMap<>();

    private final Map<String, Integer> fileChangeStore =
            new ConcurrentHashMap<>();

    /**
     * Records the change coupling between the files a and b.
     *
     * @param a The origin "a" of the diff
     * @param b The origin "b" of the diff
     */
    public void record(final String a, final String b) {
        record(new ChangeCouplingInstance(a, b));
    }

    /**
     * Records the change coupling instance.
     *
     * @param changeCouplingInstance The change coupling instance.
     */
    private void record(final ChangeCouplingInstance changeCouplingInstance) {
        // Filter out files that are coupled to deleted files
        if (!"/dev/null".equals(changeCouplingInstance.target())) {
            couplingStore.put(changeCouplingInstance,
                    couplingStore.getOrDefault(changeCouplingInstance, 0) + 1);
            fileChangeStore.put(changeCouplingInstance.origin(),
                    fileChangeStore.getOrDefault(changeCouplingInstance.origin(), 0) + 1);
        }
    }

    /**
     * Lists the change coupling metrics contained in the store.
     * <p>
     * This enforces a threshold for files that are coupled by 50% or more and
     * have at least changed 10 times.
     *
     * @param minimalChangesThreshold The minimal changes threshold
     * @param percentageThreshold     The percentage threshold
     * @return The change coupling metrics.
     */
    public Collection<ChangeCouplingMetric> list(final int minimalChangesThreshold,
                                                 final double percentageThreshold) {
        final Collection<ChangeCouplingMetric> result = new ArrayList<>();

        for (final Map.Entry<ChangeCouplingInstance, Integer> entry : couplingStore.entrySet()) {
            result.add(new ChangeCouplingMetric(
                    entry.getKey().origin(),
                    entry.getKey().target(),
                    fileChangeStore.getOrDefault(entry.getKey().origin(), 0),
                    entry.getValue(),
                    entry.getValue().doubleValue() /
                            fileChangeStore.getOrDefault(entry.getKey().origin(), 0)
            ));
        }

        return result.stream()
                .filter(m -> m.totalChanges() >= minimalChangesThreshold)
                .filter(m -> m.coupling() >= percentageThreshold)
                .toList();
    }
}
