package gavel.staticanalysis.adapter.pmd;

public record StaticAnalysisClassFinding(String className,
                                         String packageName,
                                         String description,
                                         String ruleName,
                                         String ruleDescription,
                                         Severity priority,
                                         String tool) {
}
