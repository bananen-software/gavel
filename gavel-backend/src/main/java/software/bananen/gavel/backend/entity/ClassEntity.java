package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;

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
}