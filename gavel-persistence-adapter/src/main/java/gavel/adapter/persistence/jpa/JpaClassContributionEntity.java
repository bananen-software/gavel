package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "class_contributions")
public class JpaClassContributionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class", nullable = false)
    private JpaClassEntity classField;

    @Column(name = "\"timestamp\"", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "vcs_identifier", nullable = false, length = Integer.MAX_VALUE)
    private String vcsIdentifier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "author", nullable = false)
    private JpaAuthorEntity author;

    @OneToMany(mappedBy = "contribution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<JpaClassComplexityEntity> classComplexities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "contribution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<JpaClassLinesOfCodeEntity> classLinesOfCodes = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaClassEntity getClassField() {
        return classField;
    }

    public void setClassField(JpaClassEntity classField) {
        this.classField = classField;
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

    public JpaAuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(JpaAuthorEntity author) {
        this.author = author;
    }

    public Set<JpaClassComplexityEntity> getClassComplexities() {
        return classComplexities;
    }

    public void setClassComplexities(Set<JpaClassComplexityEntity> classComplexities) {
        this.classComplexities = classComplexities;
    }

    public Set<JpaClassLinesOfCodeEntity> getClassLinesOfCodes() {
        return classLinesOfCodes;
    }

    public void setClassLinesOfCodes(Set<JpaClassLinesOfCodeEntity> classLinesOfCodes) {
        this.classLinesOfCodes = classLinesOfCodes;
    }

}