package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.backend.domain.ComplexityRating;

@Entity
@Table(name = "class_complexity")
public class ClassComplexityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ColumnDefault("0")
    @Column(name = "complexity", nullable = false)
    private Integer complexity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contribution", nullable = false)
    private ClassContributionEntity contribution;

    @Column(name = "complexity_rating", nullable = false)
    private ComplexityRating complexityRating;

    @ColumnDefault("0")
    @Column(name = "added_complexity")
    private Integer addedComplexity;

    public ClassContributionEntity getContribution() {
        return contribution;
    }

    public void setContribution(ClassContributionEntity contribution) {
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

    public ComplexityRating getComplexityRating() {
        return complexityRating;
    }

    public void setComplexityRating(ComplexityRating complexityRating) {
        this.complexityRating = complexityRating;
    }

    public Integer getAddedComplexity() {
        return addedComplexity;
    }

    public void setAddedComplexity(Integer addedComplexity) {
        this.addedComplexity = addedComplexity;
    }
}