package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "code_unit_contributions")
public class JpaCodeUnitContributionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "code_unit", nullable = false)
    private JpaCodeUnitEntity codeUnit;

    @Column(name = "\"timestamp\"", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "vcs_identifier", nullable = false, length = Integer.MAX_VALUE)
    private String vcsIdentifier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author", nullable = false)
    private JpaAuthorEntity author;

    @ColumnDefault("0")
    @Column(name = "added_loc", nullable = false)
    private Integer addedLoc;

    @ColumnDefault("0")
    @Column(name = "added_loc_comments", nullable = false)
    private Integer addedLocComments;

    @ColumnDefault("0")
    @Column(name = "added_complexity", nullable = false)
    private Integer addedComplexity;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public JpaCodeUnitEntity getCodeUnit() {
        return codeUnit;
    }

    public void setCodeUnit(JpaCodeUnitEntity codeUnit) {
        this.codeUnit = codeUnit;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getVcsIdentifier() {
        return vcsIdentifier;
    }

    public void setVcsIdentifier(String vcsIdentifier) {
        this.vcsIdentifier = vcsIdentifier;
    }

    public JpaAuthorEntity getAuthor() {
        return author;
    }

    public void setAuthor(JpaAuthorEntity author) {
        this.author = author;
    }

    public Integer getAddedLoc() {
        return addedLoc;
    }

    public void setAddedLoc(Integer addedLoc) {
        this.addedLoc = addedLoc;
    }

    public Integer getAddedLocComments() {
        return addedLocComments;
    }

    public void setAddedLocComments(Integer addedLocComments) {
        this.addedLocComments = addedLocComments;
    }

    public Integer getAddedComplexity() {
        return addedComplexity;
    }

    public void setAddedComplexity(Integer addedComplexity) {
        this.addedComplexity = addedComplexity;
    }

}