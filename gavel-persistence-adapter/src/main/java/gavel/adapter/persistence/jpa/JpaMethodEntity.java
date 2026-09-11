package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.domain.model.ClassStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "methods")
public class JpaMethodEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "md5_hash", nullable = false, length = Integer.MAX_VALUE)
    private String md5Hash;

    @Column(name = "signature", nullable = false, length = Integer.MAX_VALUE)
    private String signature;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @ColumnDefault("0")
    @Column(name = "number_of_authors", nullable = false)
    private Integer numberOfAuthors;

    @ColumnDefault("0")
    @Column(name = "number_of_changes", nullable = false)
    private Integer numberOfChanges;

    @ColumnDefault("0")
    @Column(name = "complexity", nullable = false)
    private Integer complexity;

    @ColumnDefault("0")
    @Column(name = "lines_of_code", nullable = false)
    private Integer linesOfCode;

    @Column(name = "status", nullable = false)
    private ClassStatus status;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "class")
    private JpaClassEntity classField;

    @OneToMany(mappedBy = "method", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<JpaMethodContributionEntity> contributions = new LinkedHashSet<>();

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

    public JpaClassEntity getClassField() {
        return classField;
    }

    public void setClassField(JpaClassEntity classField) {
        this.classField = classField;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public void setMd5Hash(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public Integer getNumberOfAuthors() {
        return numberOfAuthors;
    }

    public void setNumberOfAuthors(Integer numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }

    public Integer getNumberOfChanges() {
        return numberOfChanges;
    }

    public void setNumberOfChanges(Integer numberOfChanges) {
        this.numberOfChanges = numberOfChanges;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public Integer getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(Integer linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public Collection<JpaMethodContributionEntity> getContributions() {
        return contributions;
    }

    public void setContributions(Collection<JpaMethodContributionEntity> contributions) {
        this.contributions = contributions;
    }
}