package software.bananen.gavel.domain.model;

/**
 * A static analysis finding that refers to a specific class.
 *
 * @param className       The name of the class.
 * @param packageName     The name of the package.
 * @param description     The description of the finding.
 * @param ruleName        The name of the rule.
 * @param ruleDescription A description of the rule.
 * @param severity        The severity of the finding.
 * @param tool            The tool that found it.
 */
public record StaticAnalysisClassFinding(String className,
                                         String packageName,
                                         String description,
                                         String ruleName,
                                         String ruleDescription,
                                         Severity severity,
                                         String tool) {
}
