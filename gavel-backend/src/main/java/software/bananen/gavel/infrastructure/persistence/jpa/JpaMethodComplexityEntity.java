package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "method_complexity")
public class JpaMethodComplexityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private JpaMethodContributionEntity contribution;

    @Column(name = "complexity", nullable = false)
    private int complexity;

    @Column(name = "added_complexity", nullable = false)
    private int addedComplexity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaMethodContributionEntity getContribution() {
        return contribution;
    }

    public void setContribution(JpaMethodContributionEntity contribution) {
        this.contribution = contribution;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public int getAddedComplexity() {
        return addedComplexity;
    }

    public void setAddedComplexity(int addedComplexity) {
        this.addedComplexity = addedComplexity;
    }
}
