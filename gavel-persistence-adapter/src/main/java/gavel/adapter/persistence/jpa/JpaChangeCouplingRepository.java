package gavel.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
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
