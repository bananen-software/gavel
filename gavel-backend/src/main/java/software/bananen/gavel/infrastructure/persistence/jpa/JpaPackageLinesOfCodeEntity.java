package software.bananen.gavel.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.domain.model.Size;


@Entity
@Table(name = "package_lines_of_code")
public class JpaPackageLinesOfCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package", nullable = false)
    private JpaPackageEntity packageField;

    @ColumnDefault("0")
    @Column(name = "total_lines_of_code", nullable = false)
    private Integer totalLinesOfCode;

    @ColumnDefault("0")
    @Column(name = "total_lines_of_comment", nullable = false)
    private Integer totalLinesOfComment;

    @ColumnDefault("0")
    @Column(name = "comment_to_code_ratio", nullable = false)
    private Double commentToCodeRatio;

    @Column(name = "package_size", nullable = false)
    private Size packageSize;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaPackageEntity getPackageField() {
        return packageField;
    }

    public void setPackageField(JpaPackageEntity packageField) {
        this.packageField = packageField;
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

    public Size getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(Size packageSize) {
        this.packageSize = packageSize;
    }
}