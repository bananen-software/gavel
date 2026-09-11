package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.domain.model.CodeUnitType;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "code_units")
public class JpaCodeUnitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file", nullable = false)
    private JpaFileEntity file;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "type", nullable = false)
    private CodeUnitType type;

    @Column(name = "status", nullable = false)
    private ClassStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    private JpaCodeUnitEntity parent;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @Column(name = "hash", nullable = false, length = Integer.MAX_VALUE)
    private String hash;

    @ColumnDefault("0")
    @Column(name = "child_count", nullable = false)
    private Integer childCount;

    @ColumnDefault("0")
    @Column(name = "change_count", nullable = false)
    private Integer changeCount;

    @ColumnDefault("0")
    @Column(name = "author_count", nullable = false)
    private Integer authorCount;

    @ColumnDefault("0")
    @Column(name = "defect_count", nullable = false)
    private Integer defectCount;

    @ColumnDefault("0")
    @Column(name = "high_defect_count", nullable = false)
    private Integer highDefectCount;

    @ColumnDefault("0")
    @Column(name = "loc", nullable = false)
    private Integer loc;

    @ColumnDefault("0")
    @Column(name = "loc_comments", nullable = false)
    private Integer locComments;

    @ColumnDefault("0")
    @Column(name = "loc_relative", nullable = false)
    private Double locRelative;

    @ColumnDefault("0")
    @Column(name = "comment_to_code_ratio", nullable = false)
    private Double commentToCodeRatio;

    @ColumnDefault("0")
    @Column(name = "complexity", nullable = false)
    private Integer complexity;

    @ColumnDefault("0")
    @Column(name = "relative_complexity", nullable = false)
    private Double relativeComplexity;

    @OneToMany(mappedBy = "codeUnit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<JpaCodeUnitContributionEntity> codeUnitContributions = new LinkedHashSet<>();

    @OneToMany(mappedBy = "parent")
    private Set<JpaCodeUnitEntity> codeUnits = new LinkedHashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JpaFileEntity getFile() {
        return file;
    }

    public void setFile(JpaFileEntity file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CodeUnitType getType() {
        return type;
    }

    public void setType(CodeUnitType type) {
        this.type = type;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public JpaCodeUnitEntity getParent() {
        return parent;
    }

    public void setParent(JpaCodeUnitEntity parent) {
        this.parent = parent;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public Integer getChangeCount() {
        return changeCount;
    }

    public void setChangeCount(Integer changeCount) {
        this.changeCount = changeCount;
    }

    public Integer getAuthorCount() {
        return authorCount;
    }

    public void setAuthorCount(Integer authorCount) {
        this.authorCount = authorCount;
    }

    public Integer getDefectCount() {
        return defectCount;
    }

    public void setDefectCount(Integer defectCount) {
        this.defectCount = defectCount;
    }

    public Integer getHighDefectCount() {
        return highDefectCount;
    }

    public void setHighDefectCount(Integer highDefectCount) {
        this.highDefectCount = highDefectCount;
    }

    public Integer getLoc() {
        return loc;
    }

    public void setLoc(Integer loc) {
        this.loc = loc;
    }

    public Integer getLocComments() {
        return locComments;
    }

    public void setLocComments(Integer locComments) {
        this.locComments = locComments;
    }

    public Double getLocRelative() {
        return locRelative;
    }

    public void setLocRelative(Double locRelative) {
        this.locRelative = locRelative;
    }

    public Double getCommentToCodeRatio() {
        return commentToCodeRatio;
    }

    public void setCommentToCodeRatio(Double commentToCodeRatio) {
        this.commentToCodeRatio = commentToCodeRatio;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public Double getRelativeComplexity() {
        return relativeComplexity;
    }

    public void setRelativeComplexity(Double relativeComplexity) {
        this.relativeComplexity = relativeComplexity;
    }

    public Set<JpaCodeUnitContributionEntity> getCodeUnitContributions() {
        return codeUnitContributions;
    }

    public void setCodeUnitContributions(Set<JpaCodeUnitContributionEntity> codeUnitContributions) {
        this.codeUnitContributions = codeUnitContributions;
    }

    public Set<JpaCodeUnitEntity> getCodeUnits() {
        return codeUnits;
    }

    public void setCodeUnits(Set<JpaCodeUnitEntity> codeUnits) {
        this.codeUnits = codeUnits;
    }
}