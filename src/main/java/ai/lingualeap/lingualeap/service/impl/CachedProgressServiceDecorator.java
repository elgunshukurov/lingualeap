package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.config.CacheConfig.CacheNames;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.request.ExerciseProgressUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseProgressResponse;
import ai.lingualeap.lingualeap.model.response.LessonProgressResponse;
import ai.lingualeap.lingualeap.model.response.UserCourseProgressResponse;
import ai.lingualeap.lingualeap.service.ProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class CachedProgressServiceDecorator implements ProgressService {

    private final ProgressServiceImpl delegate;

    @Override
    @CacheEvict(value = {
            CacheNames.EXERCISE_PROGRESS,
            CacheNames.LESSON_PROGRESS,
            CacheNames.COURSE_PROGRESS,
            CacheNames.USER_ACTIVITY
    }, allEntries = true)
    public ExerciseProgressResponse updateExerciseProgress(ExerciseProgressUpdateRequest request) {
        log.debug("Cache evicted for exercise progress update");
        return delegate.updateExerciseProgress(request);
    }

    @Override
    @Cacheable(value = CacheNames.EXERCISE_PROGRESS, key = "'user:' + #userId")
    public List<ExerciseProgressResponse> getExerciseProgressByUserId(Long userId) {
        log.debug("Fetching exercise progress for user {} (potentially from cache)", userId);
        return delegate.getExerciseProgressByUserId(userId);
    }

    @Override
    @Cacheable(value = CacheNames.EXERCISE_PROGRESS, key = "'user:' + #userId + ':exercise:' + #exerciseId")
    public ExerciseProgressResponse getExerciseProgressByUserIdAndExerciseId(Long userId, Long exerciseId) {
        log.debug("Fetching exercise progress for user {} and exercise {} (potentially from cache)", userId, exerciseId);
        return delegate.getExerciseProgressByUserIdAndExerciseId(userId, exerciseId);
    }

    @Override
    @Cacheable(value = CacheNames.LESSON_PROGRESS, key = "'lesson:' + #lessonId")
    public LessonProgressResponse getLessonProgressById(Long lessonId) {
        log.debug("Fetching lesson progress for lesson {} (potentially from cache)", lessonId);
        return delegate.getLessonProgressById(lessonId);
    }

    @Override
    @Cacheable(value = CacheNames.EXERCISE_PROGRESS, key = "'user:' + #userId + ':lesson:' + #lessonId")
    public List<ExerciseProgressResponse> getExerciseProgressByUserIdAndLessonId(Long userId, Long lessonId) {
        log.debug("Fetching exercise progress for user {} and lesson {} (potentially from cache)", userId, lessonId);
        return delegate.getExerciseProgressByUserIdAndLessonId(userId, lessonId);
    }

    @Override
    @Cacheable(value = CacheNames.COURSE_PROGRESS, key = "'user:' + #userId + ':course:' + #courseId")
    public UserCourseProgressResponse getUserCourseProgressByUserIdAndCourseId(Long userId, Long courseId) {
        log.debug("Fetching course progress for user {} and course {} (potentially from cache)", userId, courseId);
        return delegate.getUserCourseProgressByUserIdAndCourseId(userId, courseId);
    }

    @Override
    @Cacheable(value = CacheNames.COURSE_PROGRESS, key = "'user:' + #userId")
    public List<UserCourseProgressResponse> getUserCourseProgressByUserId(Long userId) {
        log.debug("Fetching all course progress for user {} (potentially from cache)", userId);
        return delegate.getUserCourseProgressByUserId(userId);
    }

    @Override
    @Cacheable(value = CacheNames.COURSE_PROGRESS, key = "'user:' + #userId + ':status:' + #status")
    public List<UserCourseProgressResponse> getUserCourseProgressByUserIdAndStatus(Long userId, CompletionStatus status) {
        log.debug("Fetching course progress for user {} with status {} (potentially from cache)", userId, status);
        return delegate.getUserCourseProgressByUserIdAndStatus(userId, status);
    }

    @Override
    @CacheEvict(value = CacheNames.COURSE_PROGRESS, allEntries = true)
    public UserCourseProgressResponse enrollUserToCourse(Long userId, Long courseId) {
        log.debug("Cache evicted for course progress due to enrollment");
        return delegate.enrollUserToCourse(userId, courseId);
    }

    @Override
    @Cacheable(value = CacheNames.USER_ACTIVITY, key = "'user:' + #userId + ':timeRange:' + #startDate.toString() + '-' + #endDate.toString()")
    public List<ExerciseProgressResponse> getUserActivitySummary(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Fetching user activity summary for user {} between {} and {} (potentially from cache)",
                userId, startDate, endDate);
        return delegate.getUserActivitySummary(userId, startDate, endDate);
    }

    @Override
    @CacheEvict(value = CacheNames.COURSE_PROGRESS, key = "'user:' + #userId + ':course:' + #courseId")
    public UserCourseProgressResponse calculateAndUpdateUserCourseProgress(Long userId, Long courseId) {
        log.debug("Cache evicted for course progress calculation");
        return delegate.calculateAndUpdateUserCourseProgress(userId, courseId);
    }
}
