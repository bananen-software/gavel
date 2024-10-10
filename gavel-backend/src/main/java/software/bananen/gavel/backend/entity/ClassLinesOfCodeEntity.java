package software.bananen.gavel.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.backend.domain.Size;

@Entity
@Table(name = "class_lines_of_code")
public class ClassLinesOfCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ColumnDefault("0")
    @Column(name = "total_lines_of_code", nullable = false)
    private Integer totalLinesOfCode;

    @ColumnDefault("0")
    @Column(name = "total_lines_of_comment", nullable = false)
    private Integer totalLinesOfComment;

    @ColumnDefault("0")
    @Column(name = "comment_to_code_ratio", nullable = false)
    private Double commentToCodeRatio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contribution", nullable = false)
    private ClassContributionEntity contribution;

    @Column(name = "size", nullable = false)
    private Size size;

    public ClassContributionEntity getContribution() {
        return contribution;
    }

    public void setContribution(ClassContributionEntity contribution) {
        this.contribution = contribution;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public void setTotalLinesOfCode(Integer totalLinesOfCode) {
        this.totalLinesOfCode = totalLinesOfCode;
    }

    public Integer getTotalLinesOfComment() {
        return totalLinesOfComment;
    }

    public void setTotalLinesOfComment(Integer totalLinesOfComment) {
        this.totalLinesOfComment = totalLinesOfComment;
    }

    public Double getCommentToCodeRatio() {
        return commentToCodeRatio;
    }

    public void setCommentToCodeRatio(Double commentToCodeRatio) {
        this.commentToCodeRatio = commentToCodeRatio;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }
}