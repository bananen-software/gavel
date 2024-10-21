package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "class_contributions")
public class ClassContributionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "class", nullable = false)
    private ClassEntity classField;

    @Column(name = "\"timestamp\"", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "vcs_identifier", nullable = false, length = Integer.MAX_VALUE)
    private String vcsIdentifier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "author", nullable = false)
    private AuthorEntity author;

    @OneToMany(mappedBy = "contribution", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<ClassComplexityEntity> classComplexities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "contribution", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<ClassLinesOfCodeEntity> classLinesOfCodes = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassEntity getClassField() {
        return classField;
    }

    public void setClassField(ClassEntity classField) {
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

    public AuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(AuthorEntity author) {
        this.author = author;
    }

    public Set<ClassComplexityEntity> getClassComplexities() {
        return classComplexities;
    }

    public void setClassComplexities(Set<ClassComplexityEntity> classComplexities) {
        this.classComplexities = classComplexities;
    }

    public Set<ClassLinesOfCodeEntity> getClassLinesOfCodes() {
        return classLinesOfCodes;
    }

    public void setClassLinesOfCodes(Set<ClassLinesOfCodeEntity> classLinesOfCodes) {
        this.classLinesOfCodes = classLinesOfCodes;
    }

}