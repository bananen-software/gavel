package software.bananen.gavel.infrastructure.graphql;

public record FindingReadModel(Long id,
                               String description,
                               String ruleName,
                               String ruleDescription,
                               String severity,
                               String tool) {
}
