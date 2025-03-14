package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.Exercise;
import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long>, JpaSpecificationExecutor<Exercise> {
    List<Exercise> findByLessonIdOrderBySequenceAsc(Long lessonId);

    boolean existsByLessonIdAndSequence(Long lessonId, Integer sequence);

    Optional<Exercise> findByLessonIdAndSequence(Long lessonId, Integer sequence);

    List<Exercise> findByLessonIdAndStatus(Long lessonId, ExerciseStatus status);

    List<Exercise> findByLessonIdAndType(Long lessonId, ExerciseType type);

    List<Exercise> findByTypeAndStatus(ExerciseType type, ExerciseStatus status);

    @Query("SELECT e FROM Exercise e JOIN e.tags t WHERE t.id = :tagId")
    List<Exercise> findByTagId(@Param("tagId") Long tagId);

    @Query("SELECT e FROM Exercise e WHERE e.difficultyLevel = :level AND e.lesson.id = :lessonId")
    List<Exercise> findByDifficultyLevelAndLessonId(@Param("level") Integer level, @Param("lessonId") Long lessonId);

    @Query("SELECT COUNT(e) FROM Exercise e WHERE e.lesson.id = :lessonId")
    Long countByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT e FROM Exercise e WHERE e.isTemplate = true AND e.type = :type")
    List<Exercise> findTemplatesByType(@Param("type") ExerciseType type);
}
