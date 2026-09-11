package gavel.adapter.persistence.jpa;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_history")
public class JpaFileHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file")
    private JpaFileEntity file;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "vcs_identifier", nullable = false, length = Integer.MAX_VALUE)
    private String vcsIdentifier;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "author", nullable = false)
    private JpaAuthorEntity author;

    @Column(name = "complexity", nullable = false)
    private int complexity;

    @Column(name = "added_complexity", nullable = false)
    private int addedComplexity;

    @Column(name = "total_lines_of_code", nullable = false)
    private int totalLinesOfCode;

    @Column(name = "added_lines_of_code", nullable = false)
    private int addedLinesOfCode;

    @Column(name = "number_of_authors", nullable = false)
    private int numberOfAuthors;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaFileEntity getFile() {
        return file;
    }

    public void setFile(JpaFileEntity file) {
        this.file = file;
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

    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public void setTotalLinesOfCode(int totalLinesOfCode) {
        this.totalLinesOfCode = totalLinesOfCode;
    }

    public int getAddedLinesOfCode() {
        return addedLinesOfCode;
    }

    public void setAddedLinesOfCode(int addedLinesOfCode) {
        this.addedLinesOfCode = addedLinesOfCode;
    }

    public int getComplexity() {
        return complexity;
    }

    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    public int getAddedComplexity() {
        return addedComplexity;
    }

    public void setAddedComplexity(int addedComplexity) {
        this.addedComplexity = addedComplexity;
    }

    public int getNumberOfAuthors() {
        return numberOfAuthors;
    }

    public void setNumberOfAuthors(int numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }
}
