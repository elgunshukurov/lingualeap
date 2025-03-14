package ai.lingualeap.lingualeap.model.request;

import jakarta.validation.constraints.NotBlank;

public record ExerciseOptionRequest(
        @NotBlank(message = "Option content is required")
        String content,

        Boolean isCorrect,

        String feedbackText,

        Integer sequence,

        String matchKey
) {}
