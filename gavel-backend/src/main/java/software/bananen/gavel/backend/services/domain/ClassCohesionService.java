package software.bananen.gavel.backend.services.domain;

import gavel.adapter.persistence.jpa.JpaClassCohesionEntity;
import gavel.adapter.persistence.jpa.JpaClassEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import gavel.adapter.persistence.jpa.JpaClassCohesionRepository;

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
