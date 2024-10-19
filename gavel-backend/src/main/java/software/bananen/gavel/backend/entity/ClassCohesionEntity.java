package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "class_cohesion")
public class ClassCohesionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class", nullable = false)
    private ClassEntity classField;

    @ColumnDefault("0")
    @Column(name = "lcom4", nullable = false)
    private Integer lcom4;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassEntity getClassField() {
        return classField;
    }

    public void setClassField(ClassEntity classField) {
        this.classField = classField;
    }

    public Integer getLcom4() {
        return lcom4;
    }

    public void setLcom4(Integer lcom4) {
        this.lcom4 = lcom4;
    }

}