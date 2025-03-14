package ai.lingualeap.lingualeap.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Learning recommendation based on progress data.
 */
public record LearningRecommendationResponse(
        String recommendationType,
        String title,
        String description,
        double relevanceScore,
        String targetSkill,
        Long recommendedLessonId,
        String recommendedLessonTitle,
        Long recommendedCourseId,
        String recommendedCourseTitle,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate validUntil
) {
}
