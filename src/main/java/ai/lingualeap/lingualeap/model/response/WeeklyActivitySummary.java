package ai.lingualeap.lingualeap.model.response;

import java.util.Map;

/**
 * Weekly activity summary showing activity distribution.
 */
public record WeeklyActivitySummary(
        Map<String, Integer> exercisesByDay,
        Map<String, Integer> timeSpentByDay,
        String mostActiveDay,
        String leastActiveDay,
        int averageDailyExercises,
        int averageDailyMinutes
) {
}
