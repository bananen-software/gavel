package software.bananen.gavel.infrastructure.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ClassRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.ComponentDependencyMetricsRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaProjectRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.PackageRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.ProgrammingLanguageEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.ProjectEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.RelationalCohesionMetricEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.RelationalCohesionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/graphql")
public class GraphqlController {

    private final JpaProjectRepository projectRepository;
    private final PackageRepository packageRepository;
    private final ClassRepository classRepository;
    private final RelationalCohesionRepository relationalCohesionRepository;
    private final ComponentDependencyMetricsRepository componentDependencyMetricsRepository;

    public GraphqlController(@Autowired JpaProjectRepository projectRepository,
                             @Autowired PackageRepository packageRepository,
                             @Autowired ClassRepository classRepository,
                             @Autowired RelationalCohesionRepository relationalCohesionRepository,
                             @Autowired ComponentDependencyMetricsRepository componentDependencyMetricsRepository) {
        this.projectRepository = projectRepository;
        this.packageRepository = packageRepository;
        this.classRepository = classRepository;
        this.relationalCohesionRepository = relationalCohesionRepository;
        this.componentDependencyMetricsRepository = componentDependencyMetricsRepository;
    }

    @QueryMapping
    public ProjectReadModel projectById(@Argument Integer id) {
        return projectRepository.findById((long) id)
                .map(toProjectReadModel())
                .orElse(null);
    }

    @QueryMapping
    public List<PackageReadModel> packagesByProject(@Argument Integer projectId) {
        return packageRepository.findByProjectId((long) projectId)
                .stream()
                .map(toPackageReadModel())
                .toList();
    }

    @QueryMapping
    public PackageReadModel packageById(@Argument Integer id) {
        return packageRepository.findById((long) id)
                .map(toPackageReadModel())
                .orElse(null);
    }

    @QueryMapping
    public List<ClassReadModel> classesByPackage(@Argument Integer packageId) {
        return classRepository.findByPackageFieldId((long) packageId)
                .stream()
                .map(toClassReadModel())
                .toList();
    }

    @SchemaMapping(field = "packages", typeName = "Project")
    public List<PackageReadModel> projectToPackages(final ProjectReadModel project) {
        return packagesByProject(project.id);
    }

    @SchemaMapping(field = "project", typeName = "Package")
    public ProjectReadModel packageToProject(final PackageReadModel pkg) {
        return projectById(pkg.projectId);
    }

    @SchemaMapping(field = "classes", typeName = "Package")
    public List<ClassReadModel> packageToClasses(final PackageReadModel pkg) {
        return classesByPackage(pkg.id);
    }

    @SchemaMapping(field = "package", typeName = "Class")
    public PackageReadModel classToPackage(final ClassReadModel clazz) {
        return packageById(clazz.packageId);
    }

    @SchemaMapping(field = "relationalCohesion", typeName = "Package")
    public RelationalCohesionReadModel packageToRelationalCohesion(final PackageReadModel pkg) {
        return relationalCohesionRepository.findByPackageFieldId(pkg.id)
                .map(mapToRelationalCohesionReadModel())
                .orElse(null);
    }

    @SchemaMapping(field = "componentDependency", typeName = "Package")
    public ComponentDependencyReadModel packageToComponentDependency(final PackageReadModel pkg) {
        return componentDependencyMetricsRepository.findByPackageFieldId(pkg.id)
                .map(e -> new ComponentDependencyReadModel(
                        e.getAfferentCoupling(),
                        e.getEfferentCoupling(),
                        e.getAbstractness(),
                        e.getInstability(),
                        e.getDistance()
                ))
                .orElse(null);
    }

    private Function<RelationalCohesionMetricEntity, RelationalCohesionReadModel> mapToRelationalCohesionReadModel() {
        return metric -> new RelationalCohesionReadModel(
                metric.getRating().name(),
                metric.getNumberOfTypes(),
                metric.getNumberOfInternalRelationships(),
                metric.getRelationalCohesion()
        );
    }

    public record ProjectReadModel(int id,
                                   String name,
                                   String analysisStatus,
                                   String lastAnalyzed) {
    }

    public record PackageReadModel(int id,
                                   int projectId,
                                   String name,
                                   Integer complexity,
                                   String complexityRating,
                                   Double commentToCodeRatio,
                                   double defectDensity,
                                   double highDefectDensity,
                                   Integer linesOfCode,
                                   Integer linesOfComments,
                                   Integer numberOfVeryHighComplexityTypes,
                                   Integer numberOfHighComplexityTypes,
                                   Integer numberOfMediumComplexityTypes,
                                   Integer numberOfLowComplexityTypes,
                                   Integer numberOfHighPriorityFindings,
                                   Integer totalNumberOfFindings,
                                   String size,
                                   Integer numberOfTypes,
                                   int complexityOrdinal) {

    }

    public record ClassReadModel(int id,
                                 int packageId,
                                 String name,
                                 String programmingLanguage,
                                 String lastModified,
                                 int numberOfChanges,
                                 int numberOfAuthors,
                                 int complexity,
                                 String complexityRating,
                                 int totalLinesOfCode,
                                 int totalLinesOfComments,
                                 double commentToCodeRatio,
                                 int numberOfResponsibilities,
                                 String status,
                                 int totalNumberOfFindings,
                                 int numberOfHighPriorityFindings,
                                 double defectDensity,
                                 double highDefectDensity) {

    }

    public record RelationalCohesionReadModel(
            String rating,
            int numberOfTypes,
            int numberOfInternalRelationships,
            double relationalCohesion) {
    }

    public record ComponentDependencyReadModel(
            int afferentCoupling,
            int efferentCoupling,
            double abstractness,
            double instability,
            double distance) {
    }

    private static Function<PackageEntity, PackageReadModel> toPackageReadModel() {
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

    private static Function<ProjectEntity, ProjectReadModel> toProjectReadModel() {
        return e -> new ProjectReadModel(
                e.getId().intValue(),
                e.getName(),
                e.getAnalysisStatus().name(),
                Optional.ofNullable(e.getLastAnalyzed())
                        .map(LocalDateTime::toString)
                        .orElse(null)
        );
    }


    private static Function<ClassEntity, ClassReadModel> toClassReadModel() {
        return c -> new ClassReadModel(
                c.getId().intValue(),
                c.getPackageField().getId().intValue(),
                c.getName(),
                Optional.ofNullable(c.getProgrammingLanguage()).map(ProgrammingLanguageEntity::getName).orElse(null),
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
}
