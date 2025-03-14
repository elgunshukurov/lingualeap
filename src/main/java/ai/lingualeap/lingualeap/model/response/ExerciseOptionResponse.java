package ai.lingualeap.lingualeap.model.response;

public record ExerciseOptionResponse(
        Long id,
        String content,
        Boolean isCorrect,
        String feedbackText,
        Integer sequence,
        String matchKey
) {}
