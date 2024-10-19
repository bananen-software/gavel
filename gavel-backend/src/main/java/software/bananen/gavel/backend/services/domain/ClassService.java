package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.domain.ClassStatus;
import software.bananen.gavel.backend.domain.ComplexityRating;
import software.bananen.gavel.backend.domain.Size;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.PackageEntity;
import software.bananen.gavel.backend.repository.ClassRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ClassService {

    private final ClassRepository repository;

    public ClassService(@Autowired final ClassRepository repository) {
        this.repository = repository;
    }

    public ClassEntity findOrCreateClass(final PackageEntity packageEntity,
                                         final String className) {

        final Optional<ClassEntity> matchingClass =
                repository.findByPackageFieldAndName(packageEntity, className);

        return matchingClass.orElseGet(() ->
                repository.save(mapToEntity(packageEntity, className).get()));
    }

    private static Supplier<ClassEntity> mapToEntity(
            final PackageEntity packageEntity,
            final String className) {
        return () -> {
            final ClassEntity classEntity = new ClassEntity();

            classEntity.setName(className);
            classEntity.setPackageField(packageEntity);
            classEntity.setLastModified(LocalDateTime.now());
            classEntity.setComplexity(0);
            classEntity.setComplexityRating(ComplexityRating.EMPTY);
            classEntity.setNumberOfAuthors(0);
            classEntity.setNumberOfChanges(0);
            classEntity.setCommentToCodeRatio(0.0);
            classEntity.setTotalLinesOfComments(0);
            classEntity.setTotalLinesOfCode(0);
            classEntity.setSize(Size.EMPTY);
            classEntity.setNumberOfResponsibilities(0);
            classEntity.setStatus(ClassStatus.ACTIVE);

            packageEntity.getClasses().add(classEntity);
            packageEntity.setNumberOfTypes(packageEntity.getClasses().size());

            return classEntity;
        };
    }

    public void delete(final ClassEntity classEntity) {
        classEntity.setStatus(ClassStatus.DELETED);
        repository.save(classEntity);
    }
}
