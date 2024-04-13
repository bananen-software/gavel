package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

import java.util.Collection;

/**
 * A utility service that can be used to load classes.
 */
public class ClassLoadingService {

    /**
     * Loads the classes from the given list of included paths.
     *
     * @param includedPaths     The included paths.
     * @param exclusionPatterns The exclusion patterns. These can be used to
     *                          filter out /test/ or other directories you may
     *                          not want to analyze.
     * @return The loaded classes.
     */
    public JavaClasses loadFromPaths(final Collection<String> includedPaths,
                                     final Collection<String> exclusionPatterns) {
        return new ClassFileImporter()
                .withImportOption(location -> exclusionPatterns.stream().noneMatch(location::contains))
                .importPaths(includedPaths.toArray(new String[0]));
    }
}
