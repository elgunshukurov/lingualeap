package ai.lingualeap.lingualeap.model.response;

import java.util.List;

/**
 * Skill strength assessment.
 */
public record SkillStrengthResponse(
        String skillCategory,
        String skillName,
        double strengthScore,
        String strengthLevel,
        int exercisesCompleted,
        double averageScore,
        List<String> relatedLessons,
        List<String> recommendedExercises
) {
}
