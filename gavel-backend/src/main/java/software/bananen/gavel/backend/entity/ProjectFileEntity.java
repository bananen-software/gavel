package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "project_files")
public class ProjectFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private ProjectEntity project;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class", referencedColumnName = "id")
    private ClassEntity classField;

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

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public ClassEntity getClassField() {
        return classField;
    }

    public void setClassField(ClassEntity classField) {
        this.classField = classField;
    }

}