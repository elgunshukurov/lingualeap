package ai.lingualeap.lingualeap.model.request;

import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExerciseProgressUpdateRequest(
        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Exercise ID is required")
        Long exerciseId,

        @Min(value = 0, message = "Score must be at least 0")
        @Max(value = 100, message = "Score must be at most 100")
        Double score,

        @Min(value = 0, message = "Time spent must be at least 0")
        Integer timeSpent,

        String answer,

        String feedback,

        CompletionStatus status
) {}
