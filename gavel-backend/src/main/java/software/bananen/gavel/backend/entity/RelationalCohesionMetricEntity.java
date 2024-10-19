package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.backend.domain.RelationalCohesionRating;

@Entity
@Table(name = "relational_cohesion_metrics")
public class RelationalCohesionMetricEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "package", nullable = false)
    private PackageEntity packageField;

    @Column(name = "rating", nullable = false, length = Integer.MAX_VALUE)
    private RelationalCohesionRating rating;

    @ColumnDefault("0")
    @Column(name = "number_of_types", nullable = false)
    private Integer numberOfTypes;

    @ColumnDefault("0")
    @Column(name = "number_of_internal_relationships", nullable = false)
    private Integer numberOfInternalRelationships;

    @ColumnDefault("0")
    @Column(name = "relational_cohesion", nullable = false)
    private Double relationalCohesion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PackageEntity getPackageField() {
        return packageField;
    }

    public void setPackageField(PackageEntity packageField) {
        this.packageField = packageField;
    }

    public RelationalCohesionRating getRating() {
        return rating;
    }

    public void setRating(RelationalCohesionRating status) {
        this.rating = status;
    }

    public Integer getNumberOfInternalRelationships() {
        return numberOfInternalRelationships;
    }

    public void setNumberOfInternalRelationships(Integer numberOfInternalRelationships) {
        this.numberOfInternalRelationships = numberOfInternalRelationships;
    }

    public Double getRelationalCohesion() {
        return relationalCohesion;
    }

    public void setRelationalCohesion(Double relationalCohesion) {
        this.relationalCohesion = relationalCohesion;
    }

    public Integer getNumberOfTypes() {
        return numberOfTypes;
    }

    public void setNumberOfTypes(Integer numberOfTypes) {
        this.numberOfTypes = numberOfTypes;
    }
}