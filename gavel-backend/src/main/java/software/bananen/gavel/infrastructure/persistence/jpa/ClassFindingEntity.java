package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import software.bananen.gavel.domain.ports.service.Severity;

@Entity
@Table(name = "class_findings")
public class ClassFindingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class", nullable = false)
    private ClassEntity classField;

    @Column(name = "description", nullable = false, length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "rule_name", nullable = false, length = Integer.MAX_VALUE)
    private String ruleName;

    @Column(name = "rule_description", nullable = false, length = Integer.MAX_VALUE)
    private String ruleDescription;

    @Column(name = "severity", nullable = false)
    private Severity severity;

    @Column(name = "tool", nullable = false, length = Integer.MAX_VALUE)
    private String tool;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getTool() {
        return tool;
    }

    public void setTool(String tool) {
        this.tool = tool;
    }

    public ClassEntity getClassField() {
        return classField;
    }

    public void setClassField(ClassEntity classField) {
        this.classField = classField;
    }
}