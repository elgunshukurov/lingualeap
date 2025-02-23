package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long>, JpaSpecificationExecutor<Module> {
    List<Module> findByCourseIdOrderBySequenceAsc(Long courseId);

    boolean existsByCourseIdAndSequence(Long courseId, Integer sequence);

    Optional<Module> findByCourseIdAndSequence(Long courseId, Integer sequence);
}
