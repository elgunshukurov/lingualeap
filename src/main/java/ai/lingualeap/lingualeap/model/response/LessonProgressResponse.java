package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record LessonProgressResponse(
        Long id,
        LessonResponse lesson,
        Integer totalAttempts,
        Integer successfulAttempts,
        Double averageScore,
        Double bestScore,
        Integer totalTimeSpent,
        CompletionStatus status,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastAttemptAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime completionDate,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {}
