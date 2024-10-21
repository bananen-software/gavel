package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "authors")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY)
    private Set<ClassContributionEntity> classContributionEntities = new LinkedHashSet<>();

    public Set<ClassContributionEntity> getClassContributions() {
        return classContributionEntities;
    }

    public void setClassContributions(Set<ClassContributionEntity> classContributionEntities) {
        this.classContributionEntities = classContributionEntities;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}