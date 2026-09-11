package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import software.bananen.gavel.domain.model.ClassStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "files")
public class JpaFileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project")
    private JpaProjectEntity project;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<JpaClassEntity> classes = new LinkedHashSet<>();

    @Column(name = "number_of_authors", nullable = false)
    private int numberOfAuthors;

    @Column(name = "number_of_changes", nullable = false)
    private int numberOfChanges;

    @Column(name = "total_lines_of_code", nullable = false)
    private int totalLinesOfCode;

    @Column(name = "content_type", nullable = false, length = Integer.MAX_VALUE)
    private String contentType;

    @Column(name = "complexity", nullable = false)
    private int complexity;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<JpaFileHistoryEntity> fileHistoryEntities = new LinkedHashSet<>();

    @Column(name = "status", nullable = false)
    private ClassStatus status;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<JpaCodeUnitEntity> codeUnits = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public JpaProjectEntity getProject() {
        return project;
    }

    public void setProject(JpaProjectEntity project) {
        this.project = project;
    }

    public int getNumberOfAuthors() {
        return numberOfAuthors;
    }

    public void setNumberOfAuthors(int numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }

    public int getNumberOfChanges() {
        return numberOfChanges;
    }

    public void setNumberOfChanges(int numberOfChanges) {
        this.numberOfChanges = numberOfChanges;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public Set<JpaFileHistoryEntity> getFileHistoryEntities() {
        return fileHistoryEntities;
    }

    public void setFileHistoryEntities(Set<JpaFileHistoryEntity> fileContributionEntities) {
        this.fileHistoryEntities = fileContributionEntities;
    }

    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public void setTotalLinesOfCode(int totalLinesOfCode) {
        this.totalLinesOfCode = totalLinesOfCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Set<JpaClassEntity> getClasses() {
        return classes;
    }

    public void setClasses(Set<JpaClassEntity> classField) {
        this.classes = classField;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public Collection<JpaCodeUnitEntity> getCodeUnits() {
        return codeUnits;
    }

    public void setCodeUnits(Collection<JpaCodeUnitEntity> codeUnits) {
        this.codeUnits = codeUnits;
    }
}