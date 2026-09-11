package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import software.bananen.gavel.domain.model.AnalysisStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "projects")
public class JpaProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = Integer.MAX_VALUE)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace")
    private JpaWorkspaceEntity workspace;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<JpaPackageEntity> packages = new LinkedHashSet<>();

    @Column(name = "path", nullable = false, length = Integer.MAX_VALUE)
    private String path;

    @Column(name = "analysis_status")
    private AnalysisStatus analysisStatus;

    @Column(name = "last_analyzed")
    private LocalDateTime lastAnalyzed;

    @Column(name = "last_processed_commit")
    private String lastProcessedCommit;

    @Column(name = "last_processed_commit_timestamp")
    private LocalDateTime lastProcessedCommitTimestamp;
    @ColumnDefault("0")
    @Column(name = "repeated_analysis_failure_count", nullable = false)
    private Integer repeatedAnalysisFailureCount;
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
    @Column(name = "number_of_types", nullable = false)
    private Integer numberOfTypes;
    @ColumnDefault("0")
    @Column(name = "number_of_packages", nullable = false)
    private Integer numberOfPackages;
    @ColumnDefault("0")
    @Column(name = "number_of_findings", nullable = false)
    private Integer numberOfFindings;
    @ColumnDefault("0")
    @Column(name = "number_of_high_priority_findings", nullable = false)
    private Integer numberOfHighPriorityFindings;
    @ColumnDefault("0")
    @Column(name = "defect_density", nullable = false)
    private Double defectDensity;
    @ColumnDefault("0")
    @Column(name = "high_defect_density", nullable = false)
    private Double highDefectDensity;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<JpaPackageEntity> getPackages() {
        return packages;
    }

    public void setPackages(Set<JpaPackageEntity> packages) {
        this.packages = packages;
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

    public JpaWorkspaceEntity getWorkspace() {
        return workspace;
    }

    public void setWorkspace(JpaWorkspaceEntity workspace) {
        this.workspace = workspace;
    }

    public AnalysisStatus getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(AnalysisStatus analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public LocalDateTime getLastAnalyzed() {
        return lastAnalyzed;
    }

    public void setLastAnalyzed(LocalDateTime lastModified) {
        this.lastAnalyzed = lastModified;
    }

    public String getLastProcessedCommit() {
        return lastProcessedCommit;
    }

    public void setLastProcessedCommit(String lastProcessedCommit) {
        this.lastProcessedCommit = lastProcessedCommit;
    }

    public LocalDateTime getLastProcessedCommitTimestamp() {
        return lastProcessedCommitTimestamp;
    }

    public void setLastProcessedCommitTimestamp(LocalDateTime lastProcessedCommitTimestamp) {
        this.lastProcessedCommitTimestamp = lastProcessedCommitTimestamp;
    }

    public Integer getRepeatedAnalysisFailureCount() {
        return repeatedAnalysisFailureCount;
    }

    public void setRepeatedAnalysisFailureCount(Integer repeatedAnalysisFailureCount) {
        this.repeatedAnalysisFailureCount = repeatedAnalysisFailureCount;
    }

    public Integer getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public void setTotalLinesOfCode(Integer totalLinesOfCode) {
        this.totalLinesOfCode = totalLinesOfCode;
    }

    public Integer getTotalLinesOfComments() {
        return totalLinesOfComments;
    }

    public void setTotalLinesOfComments(Integer totalLinesOfComments) {
        this.totalLinesOfComments = totalLinesOfComments;
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

    public Integer getNumberOfPackages() {
        return numberOfPackages;
    }

    public void setNumberOfPackages(Integer numberOfPackages) {
        this.numberOfPackages = numberOfPackages;
    }

    public Integer getNumberOfFindings() {
        return numberOfFindings;
    }

    public void setNumberOfFindings(Integer numberOfFindings) {
        this.numberOfFindings = numberOfFindings;
    }

    public Integer getNumberOfHighPriorityFindings() {
        return numberOfHighPriorityFindings;
    }

    public void setNumberOfHighPriorityFindings(Integer numberOfHighPriorityFindings) {
        this.numberOfHighPriorityFindings = numberOfHighPriorityFindings;
    }

    public Double getDefectDensity() {
        return defectDensity;
    }

    public void setDefectDensity(Double defectDensity) {
        this.defectDensity = defectDensity;
    }

    public Double getHighDefectDensity() {
        return highDefectDensity;
    }

    public void setHighDefectDensity(Double highDefectDensity) {
        this.highDefectDensity = highDefectDensity;
    }
}