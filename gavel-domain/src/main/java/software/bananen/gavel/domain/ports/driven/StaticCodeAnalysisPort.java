package software.bananen.gavel.domain.ports.driven;

import software.bananen.gavel.domain.model.StaticAnalysisClassFinding;

import java.nio.file.Path;
import java.util.Collection;

/**
 * An adapter interface for implementations that provide insights into static
 * code analysis results.
 */
public interface StaticCodeAnalysisPort {

    /**
     * Analyzes the given project path using the static code analysis tool
     *
     * @param projectPath The project path.
     * @return The findings.
     * @throws StaticAnalysisAdapterException May be thrown in case that the analysis failed.
     */
    Collection<StaticAnalysisClassFinding> analyze(final Path projectPath) throws StaticAnalysisAdapterException;
}
