package software.bananen.gavel;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER =
            LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) throws Throwable {
        LOGGER.info("Loading config");
        final GavelConfig config = new GavelConfigLoader().loadConfig();
        LOGGER.info("Loaded config");

        File targetDirectory = new File(config.outputConfig().targetDirectory());

        LOGGER.info("Target directory is {}", targetDirectory.getAbsolutePath());

        LOGGER.info("Loading java classes from {}",
                config.analysisContext().includedPaths());
        LOGGER.info("Excluding {}", config.analysisContext().exclusionPatterns());
        JavaClasses javaClasses =
                new ClassLoadingService().loadFromPaths(config.analysisContext().includedPaths(),
                        config.analysisContext().exclusionPatterns());
        LOGGER.info("Loaded {} classes", javaClasses.size());

        JavaPackage basePackage = javaClasses.getPackage(config.analysisContext().rootPackage());
        LOGGER.info("Analyzing base package {}", basePackage.getName());

        if (!targetDirectory.exists()) {
            Files.createDirectory(targetDirectory.toPath());
        }

        recordCumulativeDependencyMetrics(basePackage, targetDirectory);
        recordComponentDependencyMetrics(basePackage, targetDirectory, config.analysisContext().resolveSubpackages());
        recordVisibilityMetrics(basePackage, targetDirectory, config.analysisContext().resolveSubpackages());
        recordDepthOfInheritanceTree(javaClasses, targetDirectory);

        new CyclicDependencyDetectionService().detect(javaClasses, basePackage);
    }

    private static void recordDepthOfInheritanceTree(final JavaClasses javaClasses,
                                                     final File targetDirectory) throws IOException {
        final Collection<DepthOfInheritanceTree> measurements = new DepthOfInheritanceTreeMetricsService().measure(javaClasses);

        final File targetFile = getFileIn(targetDirectory, "depth-of-inheritance-metrics.csv");

        final CSVWriter writer = new CSVWriter();

        writer.write(targetFile,
                List.of("class", "DIT"),
                List.of(DepthOfInheritanceTree::className,
                        DepthOfInheritanceTree::value),
                measurements);
        
        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
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

        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
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

        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
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
        LOGGER.info("Written file {}", targetFile.getAbsolutePath());
    }

    private static File getFileIn(File targetDirectory, String fileName) {
        return Paths.get(targetDirectory.getPath(), fileName).toFile();
    }
}
