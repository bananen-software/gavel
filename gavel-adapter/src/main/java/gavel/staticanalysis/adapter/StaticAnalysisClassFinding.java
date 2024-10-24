package gavel.staticanalysis.adapter;

public record StaticAnalysisClassFinding(String className,
                                         String packageName,
                                         String description,
                                         String ruleName,
                                         String ruleDescription,
                                         Severity severity,
                                         String tool) {
}
