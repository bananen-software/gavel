package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import software.bananen.gavel.domain.model.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class JpaProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace")
    private JpaWorkspaceEntity workspace;

    @OneToMany(mappedBy = "project", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaPackageEntity> packages = new LinkedHashSet<>();

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @Column(name = "analysis_status")
    private AnalysisStatus analysisStatus;

    @Column(name = "last_analyzed")
    private LocalDateTime lastAnalyzed;

    @Column(name = "last_processed_commit")
    private String lastProcessedCommit;

    @Column(name = "last_processed_commit_timestamp")
    private LocalDateTime lastProcessedCommitTimestamp;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<JpaPackageEntity> getPackages() {
        return packages;
    }

    public void setPackages(Set<JpaPackageEntity> packages) {
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

    public JpaWorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(JpaWorkspaceEntity workspace) {
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

    public String getLastProcessedCommit() {
        return lastProcessedCommit;
    }

    public void setLastProcessedCommit(String lastProcessedCommit) {
        this.lastProcessedCommit = lastProcessedCommit;
    }

    public LocalDateTime getLastProcessedCommitTimestamp() {
        return lastProcessedCommitTimestamp;
    }

    public void setLastProcessedCommitTimestamp(LocalDateTime lastProcessedCommitTimestamp) {
        this.lastProcessedCommitTimestamp = lastProcessedCommitTimestamp;
    }
}