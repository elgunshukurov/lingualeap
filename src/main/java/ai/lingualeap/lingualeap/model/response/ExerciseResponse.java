package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record ExerciseResponse(
        Long id,
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
        Long lessonId,
        String lessonTitle,
        Set<TagResponse> tags,
        List<ExerciseOptionResponse> options,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
