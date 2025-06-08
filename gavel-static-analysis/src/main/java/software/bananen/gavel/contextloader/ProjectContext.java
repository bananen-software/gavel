package software.bananen.gavel.contextloader;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;

/**
 * A record that represents the data of the project context.
 *
 * @param basePackage The base package.
 * @param javaClasses The java classes.
 */
public record ProjectContext(JavaPackage basePackage,
                             JavaClasses javaClasses) {
}
