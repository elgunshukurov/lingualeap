package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.UserProgress;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long>, JpaSpecificationExecutor<UserProgress> {
    Optional<UserProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

    List<UserProgress> findByUserIdOrderByLastAttemptAtDesc(Long userId);

    List<UserProgress> findByLessonIdAndStatus(Long lessonId, CompletionStatus status);
}
