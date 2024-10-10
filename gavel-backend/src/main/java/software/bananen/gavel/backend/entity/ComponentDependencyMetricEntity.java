package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "component_dependency_metrics")
public class ComponentDependencyMetricEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "package", nullable = false)
    private PackageEntity packageField;

    @ColumnDefault("0")
    @Column(name = "afferent_coupling", nullable = false)
    private Integer afferentCoupling;

    @ColumnDefault("0")
    @Column(name = "efferent_coupling", nullable = false)
    private Integer efferentCoupling;

    @ColumnDefault("0")
    @Column(name = "abstractness", nullable = false)
    private Double abstractness;

    @ColumnDefault("0")
    @Column(name = "instability", nullable = false)
    private Double instability;

    @ColumnDefault("0")
    @Column(name = "distance", nullable = false)
    private Double distance;

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

    public Integer getAfferentCoupling() {
        return afferentCoupling;
    }

    public void setAfferentCoupling(Integer afferentCoupling) {
        this.afferentCoupling = afferentCoupling;
    }

    public Integer getEfferentCoupling() {
        return efferentCoupling;
    }

    public void setEfferentCoupling(Integer efferentCoupling) {
        this.efferentCoupling = efferentCoupling;
    }

    public Double getAbstractness() {
        return abstractness;
    }

    public void setAbstractness(Double abstractness) {
        this.abstractness = abstractness;
    }

    public Double getInstability() {
        return instability;
    }

    public void setInstability(Double instability) {
        this.instability = instability;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

}