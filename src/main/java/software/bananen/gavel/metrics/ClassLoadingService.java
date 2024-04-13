package software.bananen.gavel.metrics;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

import java.util.Collection;

public class ClassLoadingService {

    private static final ImportOption IGNORE_TESTS =
            location -> !location.contains("/test/") && !location.contains("/integrationTest/");

    public JavaClasses loadFromPaths(final Collection<String> includedPaths,
                                     final boolean ignoreTests) {
        ClassFileImporter importer = new ClassFileImporter();

        if (ignoreTests) {
            importer = importer.withImportOption(IGNORE_TESTS);
        }

        return importer.importPaths(includedPaths.toArray(new String[0]));
    }
}
