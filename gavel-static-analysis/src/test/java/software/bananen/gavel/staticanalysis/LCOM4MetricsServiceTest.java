package software.bananen.gavel.staticanalysis;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LCOM4MetricsServiceTest {

    @Test
    public void measureLCOM4Metric() {
        final JavaClasses javaClasses = new ClassFileImporter()
                .importPackages("software.bananen.gavel.staticanalysis.examples.lcom4");

        final Collection<LCOM4Metric> result =
                new LCOM4MetricsService().measure(javaClasses);

        assertThat(result).containsExactly(
                new LCOM4Metric(
                        "software.bananen.gavel.staticanalysis.examples.lcom4",
                        "UserManagement",
                        2,
                        Set.of(
                                Set.of("logout", "checkPassword", "login"),
                                Set.of("updateProfile", "getProfile")
                        )
                )
        );
    }
}