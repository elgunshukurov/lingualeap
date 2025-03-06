package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.LessonProgress;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long>, JpaSpecificationExecutor<LessonProgress> {

    List<LessonProgress> findByLessonId(Long lessonId);

    List<LessonProgress> findByLessonIdAndStatus(Long lessonId, CompletionStatus status);

    List<LessonProgress> findByStatus(CompletionStatus status);

    List<LessonProgress> findByIsDeleted(Boolean isDeleted);

    @Query("SELECT lp FROM LessonProgress lp JOIN lp.lesson l WHERE l.module.id = :moduleId")
    List<LessonProgress> findByModuleId(Long moduleId);

    List<LessonProgress> findByLessonIdAndIsDeleted(Long lessonId, Boolean isDeleted);

    @Query("UPDATE LessonProgress lp SET lp.isDeleted = true WHERE lp.lesson.id = :lessonId")
    void softDeleteByLessonId(Long lessonId);
}
