package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.domain.model.ClassComplexityRating;


@Entity
@Table(name = "class_complexity")
public class JpaClassComplexityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ColumnDefault("0")
    @Column(name = "complexity", nullable = false)
    private Integer complexity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contribution", nullable = false)
    private JpaClassContributionEntity contribution;

    @Column(name = "complexity_rating", nullable = false)
    private ClassComplexityRating complexityRating;

    @ColumnDefault("0")
    @Column(name = "added_complexity")
    private Integer addedComplexity;

    public JpaClassContributionEntity getContribution() {
        return contribution;
    }

    public void setContribution(JpaClassContributionEntity contribution) {
        this.contribution = contribution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public ClassComplexityRating getComplexityRating() {
        return complexityRating;
    }

    public void setComplexityRating(ClassComplexityRating complexityRating) {
        this.complexityRating = complexityRating;
    }

    public Integer getAddedComplexity() {
        return addedComplexity;
    }

    public void setAddedComplexity(Integer addedComplexity) {
        this.addedComplexity = addedComplexity;
    }
}