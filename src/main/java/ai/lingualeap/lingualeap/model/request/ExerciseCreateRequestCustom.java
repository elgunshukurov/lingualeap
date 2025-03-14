package ai.lingualeap.lingualeap.model.request;

import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @deprecated Use {@link ExerciseCreateRequest} instead
 */
@Deprecated
public record ExerciseCreateRequestCustom(
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
        /**
         * Convert to the new ExerciseCreateRequest format
         */
        public ExerciseCreateRequest toNewFormat() {
                return new ExerciseCreateRequest(
                        this.title,
                        this.description,
                        this.content,
                        this.correctAnswer,
                        this.answerExplanation,
                        this.points,
                        this.timeLimit,
                        this.type,
                        this.sequence,
                        1, // Default difficulty level
                        false, // Default requiresAudio
                        false, // Default requiresSpeaking
                        true,  // Default autoGradable
                        null,  // Default maxAttempts
                        false, // Default hintAvailable
                        null,  // Default hintText
                        false, // Default isTemplate
                        this.lessonId,
                        null,  // Default tagIds
                        null   // Default options
                );
        }
}
