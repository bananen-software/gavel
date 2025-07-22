package software.bananen.gavel.infrastructure.graphql;

import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

/**
 * A utility class that provides means to map entities to read models.
 */
public final class ReadModelMappingUtils {

    private ReadModelMappingUtils() {
        // Private constructor to prevent instantiation
    }

    public static Function<JpaComponentDependencyMetricEntity, ComponentDependencyReadModel> toComponentDependencyReadModel() {
        return e -> new ComponentDependencyReadModel(
                e.getAfferentCoupling(),
                e.getEfferentCoupling(),
                e.getAbstractness(),
                e.getInstability(),
                e.getDistance()
        );
    }

    public static Function<JpaRelationalCohesionMetricEntity, RelationalCohesionReadModel> mapToRelationalCohesionReadModel() {
        return metric -> new RelationalCohesionReadModel(
                metric.getRating().name(),
                metric.getNumberOfTypes(),
                metric.getNumberOfInternalRelationships(),
                metric.getRelationalCohesion()
        );
    }

    public static Function<JpaPackageEntity, PackageReadModel> toPackageReadModel() {
        return pkg -> new PackageReadModel(
                pkg.getId().intValue(),
                pkg.getProject().getId().intValue(),
                pkg.getPackageName(),
                pkg.getComplexity(),
                pkg.getComplexityRating().name(),
                pkg.getCommentToCodeRatio(),
                pkg.getDefectDensity(),
                pkg.getHighDefectDensity(),
                pkg.getLinesOfCode(),
                pkg.getLinesOfComments(),
                pkg.getNumberOfVeryHighComplexityTypes(),
                pkg.getNumberOfHighComplexityTypes(),
                pkg.getNumberOfMediumComplexityTypes(),
                pkg.getNumberOfLowComplexityTypes(),
                pkg.getNumberOfHighPriorityFindings(),
                pkg.getTotalNumberOfFindings(),
                pkg.getSize().name(),
                pkg.getNumberOfTypes(),
                pkg.getComplexityRating().ordinal()
        );
    }

    public static Function<JpaProjectEntity, ProjectReadModel> toProjectReadModel() {
        return e -> new ProjectReadModel(
                e.getId().intValue(),
                e.getName(),
                e.getAnalysisStatus().name(),
                Optional.ofNullable(e.getLastAnalyzed())
                        .map(LocalDateTime::toString)
                        .orElse(null)
        );
    }


    public static Function<JpaClassEntity, ClassReadModel> toClassReadModel() {
        return c -> new ClassReadModel(
                c.getId().intValue(),
                c.getPackageField().getId().intValue(),
                c.getName(),
                Optional.ofNullable(c.getProgrammingLanguage()).map(JpaProgrammingLanguageEntity::getName).orElse(null),
                c.getLastModified().toString(),
                c.getNumberOfChanges(),
                c.getNumberOfAuthors(),
                c.getComplexity(),
                c.getComplexityRating().name(),
                c.getTotalLinesOfCode(),
                c.getTotalLinesOfComments(),
                c.getCommentToCodeRatio(),
                c.getNumberOfResponsibilities(),
                c.getStatus().name(),
                c.getTotalNumberOfFindings(),
                c.getNumberOfHighPriorityFindings(),
                c.getDefectDensity(),
                c.getHighDefectDensity()
        );
    }


    public static Function<JpaClassContributionEntity, ClassContributionReadModel> toClassContributionReadModel() {
        return contrib -> new ClassContributionReadModel(
                contrib.getId().intValue(),
                contrib.getTimestamp().toString(),
                contrib.getVcsIdentifier(),
                contrib.getAuthor().getId().intValue()
        );
    }

    public static Function<JpaAuthorEntity, AuthorReadModel> toAuthorReadModel() {
        return author -> new AuthorReadModel(
                author.getName(), author.getEmail()
        );
    }


    public static Function<JpaClassComplexityEntity, ClassComplexityReadModel> toClassComplexityReadModel() {
        return c -> new ClassComplexityReadModel(
                c.getComplexity(),
                c.getComplexityRating().name(),
                c.getAddedComplexity()
        );
    }

    public static Function<JpaClassFindingEntity, FindingReadModel> toFindingReadModel() {
        return finding -> new FindingReadModel(
                finding.getDescription(),
                finding.getRuleName(),
                finding.getRuleDescription(),
                finding.getSeverity().name(),
                finding.getTool()
        );
    }
}
