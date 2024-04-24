package software.bananen.gavel.metrics;

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

        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages("software.bananen.gavel.metrics.examples.relationalcohesion");

        JavaPackage pkg = javaClasses.getPackage("software.bananen.gavel.metrics.examples.relationalcohesion");

        Collection<RelationalCohesion> measurements = new RelationalCohesionMetricsService().measure(List.of(pkg));

        assertThat(measurements).containsExactly(new RelationalCohesion("software.bananen.gavel.metrics.examples.relationalcohesion",
                6,
                6,
                1.1666666666666667));
    }
}