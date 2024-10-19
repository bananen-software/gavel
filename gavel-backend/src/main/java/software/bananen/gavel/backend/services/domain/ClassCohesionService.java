package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.backend.entity.ClassCohesionEntity;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.repository.ClassCohesionRepository;

import java.util.Optional;

@Service
public class ClassCohesionService {

    private final ClassCohesionRepository repository;

    public ClassCohesionService(@Autowired ClassCohesionRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final ClassEntity classEntity,
                               final int lcom4) {
        final Optional<ClassCohesionEntity> matchingCohesionEntity =
                repository.findByClassField(classEntity);

        classEntity.setNumberOfResponsibilities(lcom4);

        if (matchingCohesionEntity.isPresent()) {
            matchingCohesionEntity.get().setLcom4(lcom4);
            repository.save(matchingCohesionEntity.get());
        } else {
            final ClassCohesionEntity cohesionEntity = new ClassCohesionEntity();
            cohesionEntity.setClassField(classEntity);
            cohesionEntity.setLcom4(lcom4);
            repository.save(cohesionEntity);
        }
    }
}
