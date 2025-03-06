package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ExerciseProgressResponse(
        Long id,
        Long userId,
        String username,
        ExerciseResponse exercise,
        Double score,
        Integer attemptCount,
        Integer timeSpent,
        String answer,
        String feedback,
        CompletionStatus status,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime completedAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime lastAttemptAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {}
