package software.bananen.gavel.staticanalysis;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RelationalCohesionMetricsServiceTest {

    @Test
    public void measureRelationalCohesion() {
        final String packageName = "software.bananen.gavel.staticanalysis.examples.relationalcohesion";

        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages(packageName);

        JavaPackage pkg = javaClasses.getPackage(packageName);

        Collection<RelationalCohesion> measurements = new RelationalCohesionMetricsService().measure(List.of(pkg));

        assertThat(measurements).containsExactly(new RelationalCohesion(packageName,
                6,
                6,
                1.1666666666666667));
    }
}