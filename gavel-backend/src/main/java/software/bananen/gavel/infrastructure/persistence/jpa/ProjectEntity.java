package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import software.bananen.gavel.domain.model.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "workspace")
    private WorkspaceEntity workspace;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<PackageEntity> packages = new LinkedHashSet<>();

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @Column(name = "analysis_status")
    private AnalysisStatus analysisStatus;

    @Column(name = "last_analyzed")
    private LocalDateTime lastAnalyzed;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<PackageEntity> getPackages() {
        return packages;
    }

    public void setPackages(Set<PackageEntity> packages) {
        this.packages = packages;
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

    public WorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(WorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public AnalysisStatus getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(AnalysisStatus analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public LocalDateTime getLastAnalyzed() {
        return lastAnalyzed;
    }

    public void setLastAnalyzed(LocalDateTime lastModified) {
        this.lastAnalyzed = lastModified;
    }
}