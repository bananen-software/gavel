package software.bananen.gavel.backend.services.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaClassCohesionEntity;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaClassCohesionRepository;
import software.bananen.gavel.infrastructure.persistence.jpa.JpaClassEntity;

import java.util.Optional;

@Service
public class ClassCohesionService {

    private final JpaClassCohesionRepository repository;

    public ClassCohesionService(@Autowired JpaClassCohesionRepository repository) {
        this.repository = repository;
    }

    public void createOrUpdate(final JpaClassEntity classEntity,
                               final int lcom4) {
        final Optional<JpaClassCohesionEntity> matchingCohesionEntity =
                repository.findByClassField(classEntity);

        classEntity.setNumberOfResponsibilities(lcom4);

        if (matchingCohesionEntity.isPresent()) {
            matchingCohesionEntity.get().setLcom4(lcom4);
            repository.save(matchingCohesionEntity.get());
        } else {
            final JpaClassCohesionEntity cohesionEntity = new JpaClassCohesionEntity();
            cohesionEntity.setClassField(classEntity);
            cohesionEntity.setLcom4(lcom4);
            repository.save(cohesionEntity);
        }
    }
}
