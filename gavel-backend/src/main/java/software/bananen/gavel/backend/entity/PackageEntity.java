package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;

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

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST)
    private Set<ClassEntity> classes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST)
    private Set<ComponentDependencyMetricEntity> componentDependencyMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST)
    private Set<CumulativeComponentDependencyEntity> cumulativeComponentDependencies = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST)
    private Set<RelationalCohesionMetricEntity> relationalCohesionMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST)
    private Set<VisibilityMetricEntity> visibilityMetrics = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST)
    private Set<PackageComplexityEntity> packageComplexityEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "packageField", cascade = CascadeType.PERSIST)
    private Set<PackageLinesOfCodeEntity> packageLinesOfCodeEntities = new LinkedHashSet<>();

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
}