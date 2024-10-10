package software.bananen.gavel.contextloader;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * A loader that can be used to load the project context.
 */
public class ProjectContextLoader {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProjectContextLoader.class);

    public ProjectContext loadProjectContext()
            throws ProjectContextLoaderException {
        final Collection<String> includedPaths = List.of("/home/dennis/workspace/github/flens-dev/pendenzenliste/");
        final Collection<String> exclusionPatterns = List.of("/test/",
                "/integrationTest/",
                "/generated/");
        final String rootPackage = "pendenzenliste";

        LOGGER.info("Loading java classes from {}", includedPaths);
        LOGGER.info("Excluding {}", exclusionPatterns);
        JavaClasses javaClasses =
                new ClassLoadingService().loadFromPaths(includedPaths, exclusionPatterns);
        LOGGER.info("Loaded {} classes", javaClasses.size());

        JavaPackage basePackage = javaClasses.getPackage(rootPackage);
        LOGGER.info("Analyzing base package {}", basePackage.getName());

        return new ProjectContext(
                basePackage,
                javaClasses
        );
    }
}
