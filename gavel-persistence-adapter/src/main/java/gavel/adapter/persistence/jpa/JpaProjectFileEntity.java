package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "project_files")
public class JpaProjectFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private JpaProjectEntity project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class", referencedColumnName = "id")
    private JpaClassEntity classField;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JpaProjectEntity getProject() {
        return project;
    }

    public void setProject(JpaProjectEntity project) {
        this.project = project;
    }

    public JpaClassEntity getClassField() {
        return classField;
    }

    public void setClassField(JpaClassEntity classField) {
        this.classField = classField;
    }

}