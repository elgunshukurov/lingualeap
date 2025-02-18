package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long>, JpaSpecificationExecutor<Lesson> {
    List<Lesson> findByModuleIdOrderBySequenceAsc(Long moduleId);
    boolean existsByModuleIdAndSequence(Long moduleId, Integer sequence);
    Optional<Lesson> findByModuleIdAndSequence(Long moduleId, Integer sequence);
    List<Lesson> findByPrerequisitesContaining(Lesson prerequisite);
}

