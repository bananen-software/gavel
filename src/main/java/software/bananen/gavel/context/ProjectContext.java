package software.bananen.gavel.context;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;
import software.bananen.gavel.config.json.GavelConfig;

import java.io.File;

/**
 * A record that represents the data of the project context.
 *
 * @param config          The config.
 * @param targetDirectory The target directory that reports are written to.
 * @param basePackage     The base package.
 * @param javaClasses     The java classes.
 */
public record ProjectContext(GavelConfig config,
                             File targetDirectory,
                             JavaPackage basePackage,
                             JavaClasses javaClasses) {
}
