package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "package_complexity")
public class JpaPackageComplexityEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package", nullable = false)
    private JpaPackageEntity packageField;

    @ColumnDefault("0")
    @Column(name = "complexity", nullable = false)
    private Integer complexity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaPackageEntity getPackageField() {
        return packageField;
    }

    public void setPackageField(JpaPackageEntity packageField) {
        this.packageField = packageField;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

}