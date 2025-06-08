package software.bananen.gavel.metrics;

/**
 * A record representing the complexity of a specific file.
 *
 * @param projectName The name of the project.
 * @param file        The file.
 * @param linesOfCode The lines of code metric.
 * @param complexity  The complexity metric.
 */
public record FileComplexity(String projectName, String file, long linesOfCode,
                             long complexity) {
}
