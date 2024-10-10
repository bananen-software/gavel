package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "methods")
public class MethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "class")
    private ClassEntity classField;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassEntity getClassField() {
        return classField;
    }

    public void setClassField(ClassEntity classField) {
        this.classField = classField;
    }

}