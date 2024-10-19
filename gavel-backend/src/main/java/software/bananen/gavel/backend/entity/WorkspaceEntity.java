package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "workspaces")
public class WorkspaceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @OneToMany(mappedBy = "workspace", cascade = CascadeType.PERSIST)
    private Set<ProjectEntity> projects = new LinkedHashSet<>();

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @Column(name = "base_package", nullable = false, length = Integer.MAX_VALUE)
    private String basePackage;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "excluded_path", nullable = false)
    private List<String> excludedPath;

    public List<String> getExcludedPath() {
        return excludedPath;
    }

    public void setExcludedPath(List<String> excludedPath) {
        this.excludedPath = excludedPath;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<ProjectEntity> getProjects() {
        return projects;
    }

    public void setProjects(Set<ProjectEntity> projects) {
        this.projects = projects;
    }

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

}