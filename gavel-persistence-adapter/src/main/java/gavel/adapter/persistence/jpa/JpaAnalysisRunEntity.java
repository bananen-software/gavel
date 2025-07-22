package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import software.bananen.gavel.domain.model.AnalysisRunStatus;

import java.util.UUID;

@Entity
public class JpaAnalysisRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private JpaWorkspaceEntity workspace;

    @Column(name = "status")
    private AnalysisRunStatus status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JpaWorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(JpaWorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public AnalysisRunStatus getStatus() {
        return status;
    }

    public void setStatus(AnalysisRunStatus status) {
        this.status = status;
    }
}
