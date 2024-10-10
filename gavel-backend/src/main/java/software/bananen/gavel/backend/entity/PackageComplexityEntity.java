package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "package_complexity")
public class PackageComplexityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package", nullable = false)
    private PackageEntity packageField;

    @ColumnDefault("0")
    @Column(name = "complexity", nullable = false)
    private Integer complexity;

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

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

}