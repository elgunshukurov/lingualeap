package ai.lingualeap.lingualeap.model.response;

/**
 * User's learning pace metrics.
 */
public record LearningPaceMetrics(
        double exercisesPerDay,
        double exercisesPerWeek,
        double minutesPerDay,
        double minutesPerWeek,
        String learningPaceCategory,
        boolean consistentLearning,
        boolean onTrackForGoals
) {
}
