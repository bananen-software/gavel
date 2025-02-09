package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassRepository
        extends JpaRepository<ClassEntity, Long> {

    Optional<ClassEntity> findByPackageFieldAndName(final PackageEntity packageEntity,
                                                    final String name);
}
