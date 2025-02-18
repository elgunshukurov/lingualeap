package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.vo.Score;

import java.time.LocalDateTime;

public record UserProgressResponse(
        Long id,
        UserResponse user,
        LessonResponse lesson,
        CompletionStatus status,
        Score score,
        LocalDateTime completedAt,
        LocalDateTime lastAttemptAt,
        Integer attemptCount
) {
}
