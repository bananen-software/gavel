package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "visibility_metrics")
public class VisibilityMetricEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "package", nullable = false)
    private PackageEntity packageField;

    @ColumnDefault("0")
    @Column(name = "relative_visibility", nullable = false)
    private Double relativeVisibility;

    @ColumnDefault("0")
    @Column(name = "average_relative_visibility", nullable = false)
    private Double averageRelativeVisibility;

    @ColumnDefault("0")
    @Column(name = "global_relative_visibility", nullable = false)
    private Double globalRelativeVisibility;

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

    public Double getRelativeVisibility() {
        return relativeVisibility;
    }

    public void setRelativeVisibility(Double relativeVisibility) {
        this.relativeVisibility = relativeVisibility;
    }

    public Double getAverageRelativeVisibility() {
        return averageRelativeVisibility;
    }

    public void setAverageRelativeVisibility(Double averageRelativeVisibility) {
        this.averageRelativeVisibility = averageRelativeVisibility;
    }

    public Double getGlobalRelativeVisibility() {
        return globalRelativeVisibility;
    }

    public void setGlobalRelativeVisibility(Double globalRelativeVisibility) {
        this.globalRelativeVisibility = globalRelativeVisibility;
    }

}