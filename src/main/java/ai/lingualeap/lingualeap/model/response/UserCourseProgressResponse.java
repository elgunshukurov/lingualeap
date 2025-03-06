package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UserCourseProgressResponse(
        Long id,
        UserResponse user,
        CourseResponse course,
        Integer completedModules,
        Integer totalModules,
        Integer completedLessons,
        Integer totalLessons,
        Integer completedExercises,
        Integer totalExercises,
        Double averageScore,
        Double completionPercentage,
        Integer totalTimeSpent,
        CompletionStatus status,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startedAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastActivityAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime completedAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {}
