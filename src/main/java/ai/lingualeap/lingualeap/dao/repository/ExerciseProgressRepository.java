package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.ExerciseProgress;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseProgressRepository extends JpaRepository<ExerciseProgress, Long>, JpaSpecificationExecutor<ExerciseProgress> {

    List<ExerciseProgress> findByUserId(Long userId);

    List<ExerciseProgress> findByExerciseId(Long exerciseId);

    Optional<ExerciseProgress> findByUserIdAndExerciseId(Long userId, Long exerciseId);

    List<ExerciseProgress> findByUserIdAndStatus(Long userId, CompletionStatus status);

    Optional<ExerciseProgress> findByUserIdAndExerciseIdAndIsDeleted(Long userId, Long exerciseId, Boolean isDeleted);

    List<ExerciseProgress> findByUserIdOrderByLastAttemptAtDesc(Long userId);

    @Query("SELECT COUNT(ep) FROM ExerciseProgress ep WHERE ep.user.id = :userId AND ep.status = 'COMPLETED' AND ep.completedAt BETWEEN :startDate AND :endDate")
    Long countCompletedExercisesByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT ep FROM ExerciseProgress ep JOIN ep.exercise e WHERE e.lesson.id = :lessonId AND ep.user.id = :userId")
    List<ExerciseProgress> findByLessonIdAndUserId(Long lessonId, Long userId);

    @Query("SELECT COUNT(ep) FROM ExerciseProgress ep JOIN ep.exercise e WHERE e.lesson.id = :lessonId AND ep.user.id = :userId AND ep.status = 'COMPLETED'")
    Long countCompletedExercisesByLessonIdAndUserId(Long lessonId, Long userId);
}
