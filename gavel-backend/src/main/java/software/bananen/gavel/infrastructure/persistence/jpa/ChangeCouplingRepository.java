package software.bananen.gavel.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface ChangeCouplingRepository
        extends JpaRepository<ChangeCouplingEntity, Long> {

    Optional<ChangeCouplingEntity> findBySourceClassAndTargetClass(
            ClassEntity sourceClass, ClassEntity targetClass);

    //Sadly this syntax is not compatible with the hibernate implementation
    @Query("SELECT cc FROM ChangeCouplingEntity cc WHERE (cc.sourceClass, cc.targetClass) IN :pairs")
    Collection<ChangeCouplingEntity> findBySourceAndTargetClassPairs(
            @Param("pairs") Collection<ClassPair> pairs);

    @Query("SELECT cc FROM ChangeCouplingEntity cc WHERE cc.sourceClass IN :sourceClasses AND cc.targetClass IN :targetClasses")
    Collection<ChangeCouplingEntity> findBySourceClassesAndTargetClasses(
            Collection<ClassEntity> sourceClasses,
            Collection<ClassEntity> targetClasses);

    record ClassPair(ClassEntity sourceClass,
                     ClassEntity targetClass) {
    }
}
