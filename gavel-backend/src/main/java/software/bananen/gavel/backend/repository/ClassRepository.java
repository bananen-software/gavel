package software.bananen.gavel.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import software.bananen.gavel.backend.entity.ClassEntity;
import software.bananen.gavel.backend.entity.PackageEntity;

import java.util.Optional;

public interface ClassRepository
        extends JpaRepository<ClassEntity, Long> {

    Optional<ClassEntity> findByPackageFieldAndName(final PackageEntity packageEntity,
                                                    final String name);
}
