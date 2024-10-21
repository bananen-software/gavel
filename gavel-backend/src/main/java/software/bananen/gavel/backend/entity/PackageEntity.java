package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import software.bananen.gavel.backend.domain.PackageComplexity;
import software.bananen.gavel.backend.domain.Size;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "packages")
public class PackageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "package", nullable = false, length = Integer.MAX_VALUE)
    private String packageName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "project")
    private ProjectEntity project;

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<ClassEntity> classes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<ComponentDependencyMetricEntity> componentDependencyMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<CumulativeComponentDependencyEntity> cumulativeComponentDependencies = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<RelationalCohesionMetricEntity> relationalCohesionMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<VisibilityMetricEntity> visibilityMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<PackageComplexityEntity> packageComplexityEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<PackageLinesOfCodeEntity> packageLinesOfCodeEntities = new LinkedHashSet<>();

    @Column(name = "size")
    private Size size;

    @Column(name = "complexity_rating")
    private PackageComplexity complexityRating;

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

    public Set<VisibilityMetricEntity> getVisibilityMetrics() {
        return visibilityMetrics;
    }

    public void setVisibilityMetrics(Set<VisibilityMetricEntity> visibilityMetrics) {
        this.visibilityMetrics = visibilityMetrics;
    }

    public Set<RelationalCohesionMetricEntity> getRelationalCohesionMetrics() {
        return relationalCohesionMetrics;
    }

    public void setRelationalCohesionMetrics(Set<RelationalCohesionMetricEntity> relationalCohesionMetrics) {
        this.relationalCohesionMetrics = relationalCohesionMetrics;
    }

    public Set<CumulativeComponentDependencyEntity> getCumulativeComponentDependencies() {
        return cumulativeComponentDependencies;
    }

    public void setCumulativeComponentDependencies(Set<CumulativeComponentDependencyEntity> cumulativeComponentDependencies) {
        this.cumulativeComponentDependencies = cumulativeComponentDependencies;
    }

    public Set<ComponentDependencyMetricEntity> getComponentDependencyMetrics() {
        return componentDependencyMetrics;
    }

    public void setComponentDependencyMetrics(Set<ComponentDependencyMetricEntity> componentDependencyMetrics) {
        this.componentDependencyMetrics = componentDependencyMetrics;
    }

    public Set<ClassEntity> getClasses() {
        return classes;
    }

    public void setClasses(Set<ClassEntity> classes) {
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

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public Set<PackageComplexityEntity> getPackageComplexityEntities() {
        return packageComplexityEntities;
    }

    public void setPackageComplexityEntities(Set<PackageComplexityEntity> packageComplexityEntities) {
        this.packageComplexityEntities = packageComplexityEntities;
    }

    public Set<PackageLinesOfCodeEntity> getPackageLinesOfCodeEntities() {
        return packageLinesOfCodeEntities;
    }

    public void setPackageLinesOfCodeEntities(Set<PackageLinesOfCodeEntity> packageLinesOfCodeEntities) {
        this.packageLinesOfCodeEntities = packageLinesOfCodeEntities;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public PackageComplexity getComplexityRating() {
        return complexityRating;
    }

    public void setComplexityRating(PackageComplexity complexityRating) {
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
}