package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.vo.LearningObjective;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record LessonResponse(
        Long id,
        String title,
        String description,
        LessonType type,
        LessonLevel level,
        LessonStatus status,
        Integer sequence,
        Integer minRequiredScore,
        Integer recommendedDuration,
        String theoryContent,
        Boolean hasAiInteraction,
        String aiPromptTemplate,
        ModuleResponse module,
        List<ExerciseResponse> exercises,
        Set<LessonResponse> prerequisites,
        Set<LearningObjective> learningObjectives,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}

