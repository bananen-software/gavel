package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.backend.domain.ClassStatus;
import software.bananen.gavel.backend.domain.ComplexityRating;
import software.bananen.gavel.backend.domain.Size;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "classes")
public class ClassEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "package")
    private PackageEntity packageField;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "programming_language")
    private ProgrammingLanguageEntity programmingLanguage;

    @OneToMany(mappedBy = "classField", cascade = CascadeType.PERSIST)
    private Set<MethodEntity> methods = new LinkedHashSet<>();

    @Column(name = "last_modified", nullable = false)
    private LocalDateTime lastModified;

    @OneToMany(mappedBy = "classField", cascade = CascadeType.PERSIST)
    private Set<ClassContributionEntity> classContributionEntities = new LinkedHashSet<>();

    @OneToMany(mappedBy = "classField", cascade = CascadeType.PERSIST)
    private Set<ClassCohesionEntity> classCohesionEntities = new LinkedHashSet<>();

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
    private ComplexityRating complexityRating;

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

    public ComplexityRating getComplexityRating() {
        return complexityRating;
    }

    public void setComplexityRating(ComplexityRating complexityRating) {
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

    public Set<ClassContributionEntity> getClassContributions() {
        return classContributionEntities;
    }

    public void setClassContributions(Set<ClassContributionEntity> classContributionEntities) {
        this.classContributionEntities = classContributionEntities;
    }

    public Set<MethodEntity> getMethods() {
        return methods;
    }

    public void setMethods(Set<MethodEntity> methods) {
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

    public PackageEntity getPackageField() {
        return packageField;
    }

    public void setPackageField(PackageEntity packageField) {
        this.packageField = packageField;
    }

    public ProgrammingLanguageEntity getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(ProgrammingLanguageEntity programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public Set<ClassCohesionEntity> getClassCohesionEntities() {
        return classCohesionEntities;
    }

    public void setClassCohesionEntities(Set<ClassCohesionEntity> classCohesionEntities) {
        this.classCohesionEntities = classCohesionEntities;
    }
}