package ai.lingualeap.lingualeap.model.request;

import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.vo.LearningObjective;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record LessonCreateRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Lesson type is required")
        LessonType type,

        @NotNull(message = "Lesson level is required")
        LessonLevel level,

        @NotNull(message = "Module ID is required")
        Long moduleId,

        Integer sequence,
        Integer minRequiredScore,
        Integer recommendedDuration,
        String theoryContent,
        Boolean hasAiInteraction,
        String aiPromptTemplate,
        Set<Long> prerequisiteIds,
        Set<LearningObjective> learningObjectives
) {}

