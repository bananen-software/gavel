package software.bananen.gavel.infrastructure.persistence.jpa;

import gavel.adapter.persistence.jpa.JpaChangeCouplingEntity;
import gavel.adapter.persistence.jpa.JpaClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface JpaChangeCouplingRepository
        extends JpaRepository<JpaChangeCouplingEntity, Long> {

    Optional<JpaChangeCouplingEntity> findBySourceClassAndTargetClass(
            JpaClassEntity sourceClass, JpaClassEntity targetClass);

    //Sadly this syntax is not compatible with the hibernate implementation
    @Query("SELECT cc FROM JpaChangeCouplingEntity cc WHERE (cc.sourceClass, cc.targetClass) IN :pairs")
    Collection<JpaChangeCouplingEntity> findBySourceAndTargetClassPairs(
            @Param("pairs") Collection<ClassPair> pairs);

    @Query("SELECT cc FROM JpaChangeCouplingEntity cc WHERE cc.sourceClass IN :sourceClasses AND cc.targetClass IN :targetClasses")
    Collection<JpaChangeCouplingEntity> findBySourceClassesAndTargetClasses(
            Collection<JpaClassEntity> sourceClasses,
            Collection<JpaClassEntity> targetClasses);

    record ClassPair(JpaClassEntity sourceClass,
                     JpaClassEntity targetClass) {
    }
}
