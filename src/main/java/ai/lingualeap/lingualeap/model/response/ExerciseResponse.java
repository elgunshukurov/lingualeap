package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;

import java.time.LocalDateTime;

public record ExerciseResponse(
        Long id,
        String title,
        String description,
        String content,
        String answerExplanation,
        Integer points,
        Integer timeLimit,
        ExerciseType type,
        ExerciseStatus status,
        Integer sequence,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
