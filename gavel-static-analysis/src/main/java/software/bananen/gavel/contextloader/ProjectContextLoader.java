package software.bananen.gavel.contextloader;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A loader that can be used to load the project context.
 */
public class ProjectContextLoader {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProjectContextLoader.class);

    public ProjectContext loadProjectContext(final ProjectContextData projectContextData)
            throws ProjectContextLoaderException {

        LOGGER.info("Loading java classes from {}", projectContextData.includedPath());
        LOGGER.info("Excluding {}", projectContextData.excludedPaths());
        JavaClasses javaClasses =
                new ClassLoadingService().loadFromPaths(List.of(projectContextData.includedPath()), projectContextData.excludedPaths());
        LOGGER.info("Loaded {} classes", javaClasses.size());

        final JavaPackage basePackage = javaClasses.getPackage(projectContextData.rootPackage());
        LOGGER.info("Analyzing base package {}", basePackage.getName());

        return new ProjectContext(
                basePackage,
                javaClasses
        );
    }
}
