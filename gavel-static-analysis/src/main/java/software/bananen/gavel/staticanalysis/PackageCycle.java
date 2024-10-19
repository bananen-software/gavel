package software.bananen.gavel.staticanalysis;

import java.util.Collection;

public record PackageCycle(Collection<String> packagesInCycle) {
}
