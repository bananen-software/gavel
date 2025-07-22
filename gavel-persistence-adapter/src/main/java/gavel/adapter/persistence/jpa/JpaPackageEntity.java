package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.domain.model.ClassStatus;
import software.bananen.gavel.domain.model.CommentToCodeRating;
import software.bananen.gavel.domain.model.PackageComplexityRating;
import software.bananen.gavel.domain.model.Size;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "packages")
public class JpaPackageEntity {
    private static final String PACKAGE_FIELD = "packageField";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "package", nullable = false, length = Integer.MAX_VALUE)
    private String packageName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project")
    private JpaProjectEntity project;

    @OneToMany(mappedBy = PACKAGE_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaClassEntity> classes = new LinkedHashSet<>();

    @OneToMany(mappedBy = PACKAGE_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaComponentDependencyMetricEntity> componentDependencyMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = PACKAGE_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaCumulativeComponentDependencyEntity> cumulativeComponentDependencies = new LinkedHashSet<>();

    @OneToMany(mappedBy = PACKAGE_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaRelationalCohesionMetricEntity> relationalCohesionMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = PACKAGE_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaVisibilityMetricEntity> visibilityMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = PACKAGE_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaPackageComplexityEntity> packageComplexityEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = PACKAGE_FIELD, cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<JpaPackageLinesOfCodeEntity> packageLinesOfCodeEntities = new LinkedHashSet<>();

    @Column(name = "size")
    private Size size;

    @Column(name = "complexity_rating")
    private PackageComplexityRating complexityRating;

    @Column(name = "lines_of_code")
    private Integer linesOfCode;

    @Column(name = "lines_of_comments")
    private Integer linesOfComments;

    @Column(name = "comment_to_code_ratio")
    private Double commentToCodeRatio;

    @Column(name = "number_of_types")
    private Integer numberOfTypes;

    @Column(name = "complexity")
    private Integer complexity;

    @Column(name = "number_of_low_complexity_types")
    private Integer numberOfLowComplexityTypes;

    @Column(name = "number_of_medium_complexity_types")
    private Integer numberOfMediumComplexityTypes;

    @Column(name = "number_of_high_complexity_types")
    private Integer numberOfHighComplexityTypes;

    @Column(name = "number_of_very_high_complexity_types")
    private Integer numberOfVeryHighComplexityTypes;

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

    public Set<JpaVisibilityMetricEntity> getVisibilityMetrics() {
        return visibilityMetrics;
    }

    public void setVisibilityMetrics(Set<JpaVisibilityMetricEntity> visibilityMetrics) {
        this.visibilityMetrics = visibilityMetrics;
    }

    public Set<JpaRelationalCohesionMetricEntity> getRelationalCohesionMetrics() {
        return relationalCohesionMetrics;
    }

    public void setRelationalCohesionMetrics(Set<JpaRelationalCohesionMetricEntity> relationalCohesionMetrics) {
        this.relationalCohesionMetrics = relationalCohesionMetrics;
    }

    public Set<JpaCumulativeComponentDependencyEntity> getCumulativeComponentDependencies() {
        return cumulativeComponentDependencies;
    }

    public void setCumulativeComponentDependencies(Set<JpaCumulativeComponentDependencyEntity> cumulativeComponentDependencies) {
        this.cumulativeComponentDependencies = cumulativeComponentDependencies;
    }

    public Set<JpaComponentDependencyMetricEntity> getComponentDependencyMetrics() {
        return componentDependencyMetrics;
    }

    public void setComponentDependencyMetrics(Set<JpaComponentDependencyMetricEntity> componentDependencyMetrics) {
        this.componentDependencyMetrics = componentDependencyMetrics;
    }

    public Set<JpaClassEntity> getClasses() {
        return classes;
    }

    public void setClasses(Set<JpaClassEntity> classes) {
        this.classes = classes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public JpaProjectEntity getProject() {
        return project;
    }

    public void setProject(JpaProjectEntity project) {
        this.project = project;
    }

    public Set<JpaPackageComplexityEntity> getPackageComplexityEntities() {
        return packageComplexityEntities;
    }

    public void setPackageComplexityEntities(Set<JpaPackageComplexityEntity> packageComplexityEntities) {
        this.packageComplexityEntities = packageComplexityEntities;
    }

    public Set<JpaPackageLinesOfCodeEntity> getPackageLinesOfCodeEntities() {
        return packageLinesOfCodeEntities;
    }

    public void setPackageLinesOfCodeEntities(Set<JpaPackageLinesOfCodeEntity> packageLinesOfCodeEntities) {
        this.packageLinesOfCodeEntities = packageLinesOfCodeEntities;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public PackageComplexityRating getComplexityRating() {
        return complexityRating;
    }

    public void setComplexityRating(PackageComplexityRating complexityRating) {
        this.complexityRating = complexityRating;
    }

    public Integer getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(Integer linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public Integer getLinesOfComments() {
        return linesOfComments;
    }

    public void setLinesOfComments(Integer linesOfComments) {
        this.linesOfComments = linesOfComments;
    }

    public Double getCommentToCodeRatio() {
        return commentToCodeRatio;
    }

    public void setCommentToCodeRatio(Double commentToCodeRatio) {
        this.commentToCodeRatio = commentToCodeRatio;
    }

    public Integer getNumberOfTypes() {
        return numberOfTypes;
    }

    public void setNumberOfTypes(Integer numberOfTypes) {
        this.numberOfTypes = numberOfTypes;
    }

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public Integer getNumberOfLowComplexityTypes() {
        return numberOfLowComplexityTypes;
    }

    public void setNumberOfLowComplexityTypes(Integer numberOfLowComplexityTypes) {
        this.numberOfLowComplexityTypes = numberOfLowComplexityTypes;
    }

    public Integer getNumberOfMediumComplexityTypes() {
        return numberOfMediumComplexityTypes;
    }

    public void setNumberOfMediumComplexityTypes(Integer numberOfMediumComplexityTypes) {
        this.numberOfMediumComplexityTypes = numberOfMediumComplexityTypes;
    }

    public Integer getNumberOfHighComplexityTypes() {
        return numberOfHighComplexityTypes;
    }

    public void setNumberOfHighComplexityTypes(Integer numberOfHighComplexityTypes) {
        this.numberOfHighComplexityTypes = numberOfHighComplexityTypes;
    }

    public Integer getNumberOfVeryHighComplexityTypes() {
        return numberOfVeryHighComplexityTypes;
    }

    public void setNumberOfVeryHighComplexityTypes(Integer numberOfVeryHighComplexityTypes) {
        this.numberOfVeryHighComplexityTypes = numberOfVeryHighComplexityTypes;
    }

    public Collection<JpaClassEntity> getActiveClasses() {
        return getClasses()
                .stream()
                .filter(e -> ClassStatus.ACTIVE.equals(e.getStatus()))
                .toList();
    }

    public CommentToCodeRating getCommentToCodeRating() {
        return commentToCodeRating;
    }

    public void setCommentToCodeRating(CommentToCodeRating commentToCodeRating) {
        this.commentToCodeRating = commentToCodeRating;
    }
}