package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class JpaCommitEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private JpaRepositoryEntity repository;

    @Column(name = "hash", nullable = false, length = 40)
    private String hash;

    @Column(name = "author_email", nullable = false, length = 255)
    private String authorEmail;

    @Column(name = "author_name", nullable = false, length = 255)
    private String authorName;

    @Column(name = "commit_timestamp", nullable = false)
    private OffsetDateTime commitTimestamp;

    @Column(name = "message", nullable = false)
    private String message;

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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public OffsetDateTime getCommitTimestamp() {
        return commitTimestamp;
    }

    public void setCommitTimestamp(OffsetDateTime commitTimestamp) {
        this.commitTimestamp = commitTimestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public OffsetDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(OffsetDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
