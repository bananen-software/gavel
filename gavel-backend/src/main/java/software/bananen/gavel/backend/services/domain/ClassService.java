package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.JpaClassEntity;
import gavel.adapter.persistence.jpa.JpaClassRepository;
import gavel.adapter.persistence.jpa.JpaPackageEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.domain.model.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ClassService {

    private final JpaClassRepository repository;

    public ClassService(@Autowired final JpaClassRepository repository) {
        this.repository = repository;
    }

    private static Supplier<JpaClassEntity> mapToEntity(
            final JpaPackageEntity packageEntity,
            final String className) {
        return () -> {
            final JpaClassEntity classEntity = new JpaClassEntity();

            classEntity.setName(className);
            classEntity.setPackageField(packageEntity);
            classEntity.setCreated(LocalDateTime.now());
            classEntity.setLastModified(LocalDateTime.now());
            classEntity.setComplexity(0);
            classEntity.setComplexityRating(ClassComplexityRating.EMPTY);
            classEntity.setNumberOfAuthors(0);
            classEntity.setNumberOfChanges(0);
            classEntity.setCommentToCodeRatio(0.0);
            classEntity.setCommentToCodeRating(CommentToCodeRating.NORMAL);
            classEntity.setTotalLinesOfComments(0);
            classEntity.setTotalLinesOfCode(0);
            classEntity.setSize(Size.EMPTY);
            classEntity.setNumberOfResponsibilities(0);
            classEntity.setStatus(ClassStatus.ACTIVE);
            classEntity.setTotalNumberOfFindings(0);
            classEntity.setNumberOfHighPriorityFindings(0);
            classEntity.setDefectDensity(0.0);
            classEntity.setHighDefectDensity(0.0);
            classEntity.setStratum(Stratum.SURFACE);

            packageEntity.getClasses().add(classEntity);
            packageEntity.setNumberOfTypes(packageEntity.getClasses().size());

            return classEntity;
        };
    }

    public JpaClassEntity findOrCreateClass(final JpaPackageEntity packageEntity,
                                            final String className) {

        final Optional<JpaClassEntity> matchingClass =
                repository.findByPackageFieldAndName(packageEntity, className);

        return matchingClass.orElseGet(() ->
                repository.save(mapToEntity(packageEntity, className).get()));
    }

    public void delete(final JpaClassEntity classEntity) {
        classEntity.setStatus(ClassStatus.DELETED);
        repository.save(classEntity);
    }

    public void save(final JpaClassEntity classEntity) {
        repository.save(classEntity);
    }
}
