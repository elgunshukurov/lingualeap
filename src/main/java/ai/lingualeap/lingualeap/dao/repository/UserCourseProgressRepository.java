package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.UserCourseProgress;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseProgressRepository extends JpaRepository<UserCourseProgress, Long>, JpaSpecificationExecutor<UserCourseProgress> {

    List<UserCourseProgress> findByUserId(Long userId);

    List<UserCourseProgress> findByCourseId(Long courseId);

    Optional<UserCourseProgress> findByUserIdAndCourseId(Long userId, Long courseId);

    List<UserCourseProgress> findByUserIdAndStatus(Long userId, CompletionStatus status);

    @Query("SELECT ucp FROM UserCourseProgress ucp WHERE ucp.user.id = :userId AND ucp.completionPercentage BETWEEN :minPercentage AND :maxPercentage")
    List<UserCourseProgress> findByUserIdAndCompletionPercentageBetween(Long userId, Double minPercentage, Double maxPercentage);

    List<UserCourseProgress> findByUserIdAndStartedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    List<UserCourseProgress> findByUserIdOrderByLastActivityAtDesc(Long userId);

    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.course.id = :courseId AND ucp.status = 'IN_PROGRESS'")
    Long countActiveUsersByCourseId(Long courseId);

    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.course.id = :courseId AND ucp.status = 'COMPLETED'")
    Long countCompletedUsersByCourseId(Long courseId);

    @Query("SELECT COUNT(ucp) FROM UserCourseProgress ucp WHERE ucp.startedAt BETWEEN :startDate AND :endDate")
    Long countCoursesStartedInDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
