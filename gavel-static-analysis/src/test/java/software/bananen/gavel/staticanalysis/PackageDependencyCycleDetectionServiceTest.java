package software.bananen.gavel.staticanalysis;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PackageDependencyCycleDetectionServiceTest {

    @Test
    public void detectCycle_packageCycle() throws Throwable {
        final String packageName = "software.bananen.gavel.staticanalysis.examples.packagecycle";

        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages(packageName);

        final JavaPackage pkg = javaClasses.getPackage(packageName);

        final Optional<PackageCycle> packageCycle =
                new PackageDependencyCycleDetectionService().detectCycles(pkg);


        assertThat(packageCycle).hasValueSatisfying(cycle ->
                assertThat(cycle.packagesInCycle()).containsExactlyInAnyOrder(
                        "software.bananen.gavel.staticanalysis.examples.packagecycle.a",
                        "software.bananen.gavel.staticanalysis.examples.packagecycle.b",
                        "software.bananen.gavel.staticanalysis.examples.packagecycle.c"));
    }

    @Test
    public void detectCycle_noPackageCycle() throws Throwable {
        final String packageName = "software.bananen.gavel.staticanalysis.examples.nopackagecycle";

        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages(packageName);

        final JavaPackage pkg = javaClasses.getPackage(packageName);

        final Optional<PackageCycle> packageCycle =
                new PackageDependencyCycleDetectionService().detectCycles(pkg);

        assertThat(packageCycle).isEmpty();
    }
}