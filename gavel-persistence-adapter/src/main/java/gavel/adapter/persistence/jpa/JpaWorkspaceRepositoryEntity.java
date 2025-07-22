package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class JpaWorkspaceRepositoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaWorkspaceEntity workspace;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaRepositoryEntity repository;

    @Column(name = "created", nullable = false)
    private OffsetDateTime created;

    @Column(name = "active")
    private boolean active;

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

    public JpaRepositoryEntity getRepository() {
        return repository;
    }

    public void setRepository(JpaRepositoryEntity repository) {
        this.repository = repository;
    }

    public OffsetDateTime getCreated() {
        return created;
    }

    public void setCreated(OffsetDateTime created) {
        this.created = created;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
