package gavel.staticanalysis.adapter.pmd;

public record StaticAnalysisFinding(String className,
                                    String packageName,
                                    String description,
                                    String ruleName,
                                    String ruleDescription,
                                    Severity priority,
                                    String tool) {
}
