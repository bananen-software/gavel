package software.bananen.gavel.context;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.bananen.gavel.config.json.GavelConfig;
import software.bananen.gavel.config.json.GavelConfigLoader;
import software.bananen.gavel.config.json.GavelConfigLoaderException;
import software.bananen.gavel.reports.csv.CSVReportFactory;

import java.io.File;

/**
 * A loader that can be used to load the project context.
 */
public class ProjectContextLoader {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProjectContextLoader.class);

    public ProjectContext loadProjectContext(final File configFile)
            throws ProjectContextLoaderException {
        try {
            LOGGER.info("Loading config");
            final GavelConfig config = new GavelConfigLoader(configFile).loadConfig();
            LOGGER.info("Loaded config: {}", config);

            final File targetDirectory =
                    new File(config.outputConfig().targetDirectory());

            LOGGER.info("Loading java classes from {}",
                    config.analysisContext().includedPaths());
            LOGGER.info("Excluding {}", config.analysisContext().exclusionPatterns());
            JavaClasses javaClasses =
                    new ClassLoadingService().loadFromPaths(config.analysisContext().includedPaths(),
                            config.analysisContext().exclusionPatterns());
            LOGGER.info("Loaded {} classes", javaClasses.size());

            JavaPackage basePackage = javaClasses.getPackage(config.analysisContext().rootPackage());
            LOGGER.info("Analyzing base package {}", basePackage.getName());

            final CSVReportFactory reportFactory = new CSVReportFactory(targetDirectory);

            return new ProjectContext(
                    config,
                    targetDirectory,
                    basePackage,
                    javaClasses,
                    reportFactory
            );
        } catch (final GavelConfigLoaderException e) {
            LOGGER.error("Failed to load config", e);
            throw new ProjectContextLoaderException("Failed to load config", e);
        }
    }
}
