package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "method_lines_of_code")
public class JpaMethodLinesOfCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private JpaMethodContributionEntity contribution;

    @Column(name = "lines_of_code", nullable = false)
    private int linesOfCode;

    @Column(name = "added_lines_of_code", nullable = false)
    private int addedLinesOfCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaMethodContributionEntity getContribution() {
        return contribution;
    }

    public void setContribution(JpaMethodContributionEntity contribution) {
        this.contribution = contribution;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int complexity) {
        this.linesOfCode = complexity;
    }

    public int getAddedLinesOfCode() {
        return addedLinesOfCode;
    }

    public void setAddedLinesOfCode(int addedComplexity) {
        this.addedLinesOfCode = addedComplexity;
    }
}
