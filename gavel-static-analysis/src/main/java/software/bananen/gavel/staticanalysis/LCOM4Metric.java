package software.bananen.gavel.staticanalysis;

import java.util.Set;

public record LCOM4Metric(String packageName,
                          String className,
                          int value,
                          Set<Set<String>> responsibilityClusters) {
}
