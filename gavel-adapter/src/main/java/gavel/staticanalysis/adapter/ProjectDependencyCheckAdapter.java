package gavel.staticanalysis.adapter;

import java.io.File;
import java.util.Collection;

/**
 * The interface for adapters that implement a project dependency check.
 * <p>
 * Dependency checks are used to gather information about the dependencies of a
 * project and unveil vulnerabilities within the used dependencies for further
 * investigation.
 */
public interface ProjectDependencyCheckAdapter {

    /**
     * Checks the dependencies of the given project path.
     *
     * @param projectPath The project path.
     * @return The projects dependencies.
     * @throws StaticAnalysisAdapterException Might be thrown in case that the analysis failed.
     */
    Collection<ProjectDependency> checkDependencies(final File projectPath) throws StaticAnalysisAdapterException;
}
