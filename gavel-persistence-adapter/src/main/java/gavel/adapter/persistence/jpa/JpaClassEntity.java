package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.domain.model.ClassComplexityRating;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.domain.model.CommentToCodeRating;
import software.bananen.gavel.domain.model.Size;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "classes")
public class JpaClassEntity {
    private static final String CLASS_FIELD = "classField";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "package")
    private JpaPackageEntity packageField;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "programming_language")
    private JpaProgrammingLanguageEntity programmingLanguage;

    @OneToMany(mappedBy = CLASS_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaMethodEntity> methods = new LinkedHashSet<>();

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @OneToMany(mappedBy = CLASS_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaClassContributionEntity> classContributionEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = CLASS_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaClassCohesionEntity> classCohesionEntities = new LinkedHashSet<>();

    @ColumnDefault("0")
    @Column(name = "number_of_authors", nullable = false)
    private Integer numberOfAuthors;

    @ColumnDefault("0")
    @Column(name = "number_of_changes", nullable = false)
    private Integer numberOfChanges;

    @ColumnDefault("0")
    @Column(name = "complexity", nullable = false)
    private Integer complexity;

    @Column(name = "complexity_rating", nullable = false)
    private ClassComplexityRating complexityRating;

    @ColumnDefault("0")
    @Column(name = "total_lines_of_code", nullable = false)
    private Integer totalLinesOfCode;

    @ColumnDefault("0")
    @Column(name = "total_lines_of_comments", nullable = false)
    private Integer totalLinesOfComments;

    @ColumnDefault("0")
    @Column(name = "comment_to_code_ratio", nullable = false)
    private Double commentToCodeRatio;

    @ColumnDefault("0")
    @Column(name = "number_of_responsibilities", nullable = false)
    private Integer numberOfResponsibilities;

    @Column(name = "size", nullable = false)
    private Size size;

    @Column(name = "status", nullable = false)
    private ClassStatus status;

    @OneToMany(mappedBy = CLASS_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaClassFindingEntity> classFindingEntities = new LinkedHashSet<>();

    @ColumnDefault("0")
    @Column(name = "total_number_of_findings", nullable = false)
    private Integer totalNumberOfFindings;

    @ColumnDefault("0")
    @Column(name = "number_of_high_priority_findings", nullable = false)
    private Integer numberOfHighPriorityFindings;

    @ColumnDefault("0")
    @Column(name = "defect_density", nullable = false)
    private double defectDensity;

    @ColumnDefault("0")
    @Column(name = "high_defect_density", nullable = false)
    private double highDefectDensity;

    @Column(name = "comment_to_code_rating", nullable = false)
    private CommentToCodeRating commentToCodeRating;

    public double getHighDefectDensity() {
        return highDefectDensity;
    }

    public void setHighDefectDensity(double highDefectDensity) {
        this.highDefectDensity = highDefectDensity;
    }

    public double getDefectDensity() {
        return defectDensity;
    }

    public void setDefectDensity(double defectDensity) {
        this.defectDensity = defectDensity;
    }

    public Integer getNumberOfHighPriorityFindings() {
        return numberOfHighPriorityFindings;
    }

    public void setNumberOfHighPriorityFindings(Integer numberOfHighPriorityFindings) {
        this.numberOfHighPriorityFindings = numberOfHighPriorityFindings;
    }

    public Integer getTotalNumberOfFindings() {
        return totalNumberOfFindings;
    }

    public void setTotalNumberOfFindings(Integer totalNumberOfFindings) {
        this.totalNumberOfFindings = totalNumberOfFindings;
    }

    public ClassStatus getStatus() {
        return status;
    }

    public void setStatus(ClassStatus status) {
        this.status = status;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public Integer getNumberOfResponsibilities() {
        return numberOfResponsibilities;
    }

    public void setNumberOfResponsibilities(Integer numberOfResponsibilities) {
        this.numberOfResponsibilities = numberOfResponsibilities;
    }

    public Double getCommentToCodeRatio() {
        return commentToCodeRatio;
    }

    public void setCommentToCodeRatio(Double commentToCodeRatio) {
        this.commentToCodeRatio = commentToCodeRatio;
    }

    public Integer getTotalLinesOfComments() {
        return totalLinesOfComments;
    }

    public void setTotalLinesOfComments(Integer totalLinesOfComments) {
        this.totalLinesOfComments = totalLinesOfComments;
    }

    public Integer getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public void setTotalLinesOfCode(Integer totalLinesOfCode) {
        this.totalLinesOfCode = totalLinesOfCode;
    }

    public ClassComplexityRating getComplexityRating() {
        return complexityRating;
    }

    public void setComplexityRating(ClassComplexityRating complexityRating) {
        this.complexityRating = complexityRating;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public Integer getNumberOfChanges() {
        return numberOfChanges;
    }

    public void setNumberOfChanges(Integer numberOfChanges) {
        this.numberOfChanges = numberOfChanges;
    }

    public Integer getNumberOfAuthors() {
        return numberOfAuthors;
    }

    public void setNumberOfAuthors(Integer numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }

    public Set<JpaClassContributionEntity> getClassContributions() {
        return classContributionEntities;
    }

    public void setClassContributions(Set<JpaClassContributionEntity> classContributionEntities) {
        this.classContributionEntities = classContributionEntities;
    }

    public Set<JpaMethodEntity> getMethods() {
        return methods;
    }

    public void setMethods(Set<JpaMethodEntity> methods) {
        this.methods = methods;
    }

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

    public JpaPackageEntity getPackageField() {
        return packageField;
    }

    public void setPackageField(JpaPackageEntity packageField) {
        this.packageField = packageField;
    }

    public JpaProgrammingLanguageEntity getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(JpaProgrammingLanguageEntity programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Set<JpaClassCohesionEntity> getClassCohesionEntities() {
        return classCohesionEntities;
    }

    public void setClassCohesionEntities(Set<JpaClassCohesionEntity> classCohesionEntities) {
        this.classCohesionEntities = classCohesionEntities;
    }

    public Set<JpaClassFindingEntity> getClassFindingEntities() {
        return classFindingEntities;
    }

    public void setClassFindingEntities(Set<JpaClassFindingEntity> classFindingEntities) {
        this.classFindingEntities = classFindingEntities;
    }

    public CommentToCodeRating getCommentToCodeRating() {
        return commentToCodeRating;
    }

    public void setCommentToCodeRating(CommentToCodeRating commentToCodeRating) {
        this.commentToCodeRating = commentToCodeRating;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}