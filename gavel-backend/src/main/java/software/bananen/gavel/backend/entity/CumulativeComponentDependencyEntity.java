package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "cumulative_component_dependencies")
public class CumulativeComponentDependencyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "package", nullable = false)
    private PackageEntity packageField;

    @ColumnDefault("0")
    @Column(name = "cumulative", nullable = false)
    private Integer cumulative;

    @ColumnDefault("0")
    @Column(name = "average", nullable = false)
    private Double average;

    @ColumnDefault("0")
    @Column(name = "relative_average", nullable = false)
    private Double relativeAverage;

    @ColumnDefault("0")
    @Column(name = "normalized", nullable = false)
    private Double normalized;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PackageEntity getPackageField() {
        return packageField;
    }

    public void setPackageField(PackageEntity packageField) {
        this.packageField = packageField;
    }

    public Integer getCumulative() {
        return cumulative;
    }

    public void setCumulative(Integer cumulative) {
        this.cumulative = cumulative;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Double getRelativeAverage() {
        return relativeAverage;
    }

    public void setRelativeAverage(Double relativeAverage) {
        this.relativeAverage = relativeAverage;
    }

    public Double getNormalized() {
        return normalized;
    }

    public void setNormalized(Double normalized) {
        this.normalized = normalized;
    }

}