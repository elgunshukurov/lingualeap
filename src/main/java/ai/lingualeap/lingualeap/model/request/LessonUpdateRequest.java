package ai.lingualeap.lingualeap.model.request;

import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.vo.LearningObjective;

import java.util.Set;

public record LessonUpdateRequest(
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
        Set<Long> prerequisiteIds,
        Set<LearningObjective> learningObjectives
) {
}
