package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "change_coupling")
public class ChangeCouplingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_class", nullable = false)
    private ClassEntity sourceClass;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_class", nullable = false)
    private ClassEntity targetClass;

    @ColumnDefault("0")
    @Column(name = "coupled_changes", nullable = false)
    private Integer coupledChanges;

    @ColumnDefault("0")
    @Column(name = "total_changes", nullable = false)
    private Integer totalChanges;

    @ColumnDefault("0")
    @Column(name = "change_coupling", nullable = false)
    private Double changeCoupling;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassEntity getSourceClass() {
        return sourceClass;
    }

    public void setSourceClass(ClassEntity sourceClass) {
        this.sourceClass = sourceClass;
    }

    public ClassEntity getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(ClassEntity targetClass) {
        this.targetClass = targetClass;
    }

    public Integer getCoupledChanges() {
        return coupledChanges;
    }

    public void setCoupledChanges(Integer coupledChanges) {
        this.coupledChanges = coupledChanges;
    }

    public Integer getTotalChanges() {
        return totalChanges;
    }

    public void setTotalChanges(Integer totalChanges) {
        this.totalChanges = totalChanges;
    }

    public Double getChangeCoupling() {
        return changeCoupling;
    }

    public void setChangeCoupling(Double changeCoupling) {
        this.changeCoupling = changeCoupling;
    }

}