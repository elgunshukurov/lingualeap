package ai.lingualeap.lingualeap.model.request;

import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Set;

public record ExerciseUpdateRequest(
        String title,
        String description,
        String content,
        String correctAnswer,
        String answerExplanation,
        Integer points,
        Integer timeLimit,
        ExerciseType type,
        ExerciseStatus status,
        Integer sequence,
        Integer difficultyLevel,
        Boolean requiresAudio,
        Boolean requiresSpeaking,
        Boolean autoGradable,
        Integer maxAttempts,
        Boolean hintAvailable,
        String hintText,
        Boolean isTemplate,
        Set<Long> tagIds,
        @Valid
        List<ExerciseOptionRequest> options
) {}
