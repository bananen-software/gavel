package software.bananen.gavel.tracing;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import software.bananen.gavel.config.json.RuntimeDependenciesConfig;

import java.util.Collections;

class RuntimeDependencyTracingServiceTest {

    @Test
    public void hiMom() {
        final var javaClasses = new ClassFileImporter()
                .importPackages("software.bananen.gavel.tracing.examples.tracing");

        final var service = new RuntimeDependencyTracingService();

        service.traceRuntimeDependencies(javaClasses,
                new RuntimeDependenciesConfig(Collections.emptyList()));

    }
}