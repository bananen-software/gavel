package software.bananen.gavel;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import software.bananen.gavel.config.json.GavelConfig;
import software.bananen.gavel.config.json.GavelConfigLoader;
import software.bananen.gavel.detection.CyclicDependencyDetectionService;
import software.bananen.gavel.metrics.*;
import software.bananen.gavel.writer.csv.CSVWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

public class Application {
    public static void main(final String[] args) throws Throwable {
        final GavelConfig config = new GavelConfigLoader().loadConfig();

        File targetDirectory = new File(config.outputConfig().targetDirectory());

        JavaClasses javaClasses =
                new ClassLoadingService().loadFromPaths(config.analysisContext().includedPaths(),
                        config.analysisContext().exclusionPatterns());

        JavaPackage basePackage = javaClasses.getPackage(config.analysisContext().rootPackage());

        if (!targetDirectory.exists()) {
            Files.createDirectory(targetDirectory.toPath());
        }

        recordCumulativeDependencyMetrics(basePackage, targetDirectory);
        recordComponentDependencyMetrics(basePackage, targetDirectory, config.analysisContext().resolveSubpackages());
        recordVisibilityMetrics(basePackage, targetDirectory, config.analysisContext().resolveSubpackages());

        new CyclicDependencyDetectionService().detect(javaClasses, basePackage);
    }

    private static void recordVisibilityMetrics(JavaPackage basePackage,
                                                File targetDirectory,
                                                boolean resolveSubpackages) throws IOException {
        final Collection<ComponentVisibility> measurements =
                new ComponentVisibilityMetricsService().measure(basePackage, resolveSubpackages);

        final File targetFile = getFileIn(targetDirectory, "visibility-metrics.csv");

        final CSVWriter writer = new CSVWriter();

        writer.write(targetFile,
                List.of("package", "RV", "ARV", "GRV"),
                List.of(ComponentVisibility::packageName,
                        ComponentVisibility::relativeVisibility,
                        ComponentVisibility::averageRelativeVisibility,
                        ComponentVisibility::globalRelativeVisibility),
                measurements);
    }

    private static void recordComponentDependencyMetrics(JavaPackage basePackage,
                                                         File targetDirectory,
                                                         boolean resolveSubpackages) throws IOException {
        final Collection<ComponentDependency> measurements =
                new ComponentDependencyMetricsService().measure(basePackage, resolveSubpackages);

        final File targetFile = getFileIn(targetDirectory, "component-dependency-metrics.csv");

        final CSVWriter writer = new CSVWriter();

        writer.write(targetFile,
                List.of("package", "Ce", "Ca", "I", "A", "D"),
                List.of(ComponentDependency::packageName,
                        ComponentDependency::efferentCoupling,
                        ComponentDependency::afferentCoupling,
                        ComponentDependency::instability,
                        ComponentDependency::abstractness,
                        ComponentDependency::normalizedDistanceFromMainSequence),
                measurements);
    }

    private static void recordCumulativeDependencyMetrics(final JavaPackage basePackage, File targetDirectory) throws IOException {
        CumulativeComponentDependency measurement = new CumulativeComponentDependencyMetricsService().measure(basePackage);

        File targetFile = getFileIn(targetDirectory, "cumulative-dependency-metrics.csv");

        CSVWriter out = new CSVWriter();

        out.write(targetFile,
                List.of("CCD", "ACD", "RACD", "NCCD"),
                List.of(CumulativeComponentDependency::cumulative,
                        CumulativeComponentDependency::average,
                        CumulativeComponentDependency::relativeAverage,
                        CumulativeComponentDependency::normalized),
                List.of(measurement));
    }

    private static File getFileIn(File targetDirectory, String fileName) {
        return Paths.get(targetDirectory.getPath(), fileName).toFile();
    }
}