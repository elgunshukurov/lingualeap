package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long>, JpaSpecificationExecutor<Exercise> {
    List<Exercise> findByLessonIdOrderBySequenceAsc(Long lessonId);

    boolean existsByLessonIdAndSequence(Long lessonId, Integer sequence);

    Optional<Exercise> findByLessonIdAndSequence(Long lessonId, Integer sequence);
}
