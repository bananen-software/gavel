package software.bananen.gavel.infrastructure.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.bananen.gavel.infrastructure.persistence.jpa.*;

import java.util.Collection;
import java.util.List;

import static software.bananen.gavel.infrastructure.graphql.ReadModelMappingUtils.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping(value = "/graphql")
public class GraphqlController {

    private static final String PACKAGE_TYPE_NAME = "Package";

    private final JpaProjectRepository projectRepository;
    private final JpaPackageRepository packageRepository;
    private final JpaClassRepository classRepository;
    private final JpaRelationalCohesionRepository relationalCohesionRepository;
    private final JpaComponentDependencyMetricsRepository componentDependencyMetricsRepository;
    private final JpaClassContributionRepository classContributionRepository;
    private final JpaAuthorRepository authorRepository;
    private final JpaClassComplexityRepository classComplexityRepository;
    private final JpaClassFindingRepository classFindingRepository;

    public GraphqlController(@Autowired JpaProjectRepository projectRepository,
                             @Autowired JpaPackageRepository packageRepository,
                             @Autowired JpaClassRepository classRepository,
                             @Autowired JpaRelationalCohesionRepository relationalCohesionRepository,
                             @Autowired JpaComponentDependencyMetricsRepository componentDependencyMetricsRepository,
                             @Autowired JpaClassContributionRepository classContributionRepository,
                             @Autowired JpaAuthorRepository authorRepository,
                             @Autowired JpaClassComplexityRepository classComplexityRepository,
                             @Autowired JpaClassFindingRepository classFindingRepository) {
        this.projectRepository = projectRepository;
        this.packageRepository = packageRepository;
        this.classRepository = classRepository;
        this.relationalCohesionRepository = relationalCohesionRepository;
        this.componentDependencyMetricsRepository = componentDependencyMetricsRepository;
        this.classContributionRepository = classContributionRepository;
        this.authorRepository = authorRepository;
        this.classComplexityRepository = classComplexityRepository;
        this.classFindingRepository = classFindingRepository;
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
    public ClassReadModel classById(@Argument Integer classId) {
        return classRepository.findById((long) classId)
                .map(toClassReadModel())
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
        return packagesByProject(project.id());
    }

    @SchemaMapping(field = "project", typeName = PACKAGE_TYPE_NAME)
    public ProjectReadModel packageToProject(final PackageReadModel pkg) {
        return projectById(pkg.projectId());
    }

    @SchemaMapping(field = "classes", typeName = PACKAGE_TYPE_NAME)
    public List<ClassReadModel> packageToClasses(final PackageReadModel pkg) {
        return classesByPackage(pkg.id());
    }

    @SchemaMapping(field = "package", typeName = "Class")
    public PackageReadModel classToPackage(final ClassReadModel clazz) {
        return packageById(clazz.packageId());
    }

    @SchemaMapping(field = "relationalCohesion", typeName = PACKAGE_TYPE_NAME)
    public RelationalCohesionReadModel packageToRelationalCohesion(final PackageReadModel pkg) {
        return relationalCohesionRepository.findByPackageFieldId(pkg.id())
                .map(mapToRelationalCohesionReadModel())
                .orElse(null);
    }

    @SchemaMapping(field = "componentDependency", typeName = PACKAGE_TYPE_NAME)
    public ComponentDependencyReadModel packageToComponentDependency(final PackageReadModel pkg) {
        return componentDependencyMetricsRepository.findByPackageFieldId(pkg.id())
                .map(toComponentDependencyReadModel())
                .orElse(null);
    }

    @SchemaMapping(field = "contributions", typeName = "Class")
    public Collection<ClassContributionReadModel> classToContribution(final ClassReadModel clazz) {
        return classContributionRepository.findByClassFieldId(clazz.id())
                .stream()
                .map(toClassContributionReadModel())
                .toList();
    }

    @SchemaMapping(field = "author", typeName = "ClassContribution")
    public AuthorReadModel classContributionToAuthor(final ClassContributionReadModel contribution) {
        return authorRepository.findById((long) contribution.authorId())
                .map(toAuthorReadModel())
                .orElse(null);
    }

    @SchemaMapping(field = "complexity", typeName = "ClassContribution")
    public ClassComplexityReadModel classContributionToComplexity(final ClassContributionReadModel contribution) {
        return classComplexityRepository.findByContributionId(contribution.id())
                .stream()
                .findFirst()
                .map(toClassComplexityReadModel()).orElse(null);
    }

    @SchemaMapping(field = "findings", typeName = "Class")
    public Collection<FindingReadModel> classToFindings(final ClassReadModel clazz) {
        return classFindingRepository.findByClassFieldId((long) clazz.id())
                .stream()
                .map(toFindingReadModel())
                .toList();
    }
}
