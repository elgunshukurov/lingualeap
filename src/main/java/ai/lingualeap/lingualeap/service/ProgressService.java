package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.request.ExerciseProgressUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseProgressResponse;
import ai.lingualeap.lingualeap.model.response.LessonProgressResponse;
import ai.lingualeap.lingualeap.model.response.UserCourseProgressResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface ProgressService {

    ExerciseProgressResponse updateExerciseProgress(ExerciseProgressUpdateRequest request);

    List<ExerciseProgressResponse> getExerciseProgressByUserId(Long userId);

    ExerciseProgressResponse getExerciseProgressByUserIdAndExerciseId(Long userId, Long exerciseId);

    LessonProgressResponse getLessonProgressById(Long lessonId);

    List<ExerciseProgressResponse> getExerciseProgressByUserIdAndLessonId(Long userId, Long lessonId);

    UserCourseProgressResponse getUserCourseProgressByUserIdAndCourseId(Long userId, Long courseId);

    List<UserCourseProgressResponse> getUserCourseProgressByUserId(Long userId);

    List<UserCourseProgressResponse> getUserCourseProgressByUserIdAndStatus(Long userId, CompletionStatus status);

    UserCourseProgressResponse enrollUserToCourse(Long userId, Long courseId);

    List<ExerciseProgressResponse> getUserActivitySummary(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    UserCourseProgressResponse calculateAndUpdateUserCourseProgress(Long userId, Long courseId);
}
