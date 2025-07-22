package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class JpaFileIdentityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaRepositoryEntity repository;

    @Column(name = "content_hash", nullable = false, length = 64)
    private String contentHash;

    @Column(name = "mime_type", nullable = false, length = 64)
    private String mimeType;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaCommitEntity firstSeenCommit;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaCommitEntity lastSeenCommit;

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

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public JpaCommitEntity getFirstSeenCommit() {
        return firstSeenCommit;
    }

    public void setFirstSeenCommit(JpaCommitEntity firstSeenCommit) {
        this.firstSeenCommit = firstSeenCommit;
    }

    public JpaCommitEntity getLastSeenCommit() {
        return lastSeenCommit;
    }

    public void setLastSeenCommit(JpaCommitEntity lastSeenCommit) {
        this.lastSeenCommit = lastSeenCommit;
    }

    public OffsetDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(OffsetDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
