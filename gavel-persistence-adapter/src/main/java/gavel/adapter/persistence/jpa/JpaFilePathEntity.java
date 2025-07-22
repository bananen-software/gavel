package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import software.bananen.gavel.domain.model.DiffType;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class JpaFilePathEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaRepositoryEntity repository;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaFileIdentityEntity fileIdentity;

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaCommitEntity commit;

    @Column(name = "operation")
    private DiffType operation;

    @Column(name = "previous_path", nullable = false, length = Integer.MAX_VALUE)
    private String previousPath;

    @Column(name = "created_timestamp", nullable = false)
    private OffsetDateTime createdTimestamp;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JpaRepositoryEntity getRepository() {
        return repository;
    }

    public void setRepository(JpaRepositoryEntity repository) {
        this.repository = repository;
    }

    public JpaFileIdentityEntity getFileIdentity() {
        return fileIdentity;
    }

    public void setFileIdentity(JpaFileIdentityEntity fileIdentity) {
        this.fileIdentity = fileIdentity;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JpaCommitEntity getCommit() {
        return commit;
    }

    public void setCommit(JpaCommitEntity commit) {
        this.commit = commit;
    }

    public DiffType getOperation() {
        return operation;
    }

    public void setOperation(DiffType operation) {
        this.operation = operation;
    }

    public String getPreviousPath() {
        return previousPath;
    }

    public void setPreviousPath(String previousPath) {
        this.previousPath = previousPath;
    }

    public OffsetDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(OffsetDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
