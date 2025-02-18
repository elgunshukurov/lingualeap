package ai.lingualeap.lingualeap.model.request;

import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExerciseCreateRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotBlank(message = "Content is required")
        String content,

        String correctAnswer,
        String answerExplanation,

        @NotNull(message = "Points are required")
        Integer points,

        Integer timeLimit,

        @NotNull(message = "Exercise type is required")
        ExerciseType type,

        Integer sequence,

        @NotNull(message = "Lesson ID is required")
        Long lessonId
) {
}
