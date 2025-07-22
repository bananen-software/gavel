package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "method_contributions")
public class JpaMethodContributionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method")
    private JpaMethodEntity method;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private JpaAuthorEntity authorEntity;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "vcs_identifier")
    private String vcsIdentifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaMethodEntity getMethod() {
        return method;
    }

    public void setMethod(JpaMethodEntity method) {
        this.method = method;
    }

    public JpaAuthorEntity getAuthorEntity() {
        return authorEntity;
    }

    public void setAuthorEntity(JpaAuthorEntity authorEntity) {
        this.authorEntity = authorEntity;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getVcsIdentifier() {
        return vcsIdentifier;
    }

    public void setVcsIdentifier(String vcsIdentifier) {
        this.vcsIdentifier = vcsIdentifier;
    }
}
