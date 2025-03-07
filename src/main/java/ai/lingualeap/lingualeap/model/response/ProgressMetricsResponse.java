package ai.lingualeap.lingualeap.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive progress metrics for a user's learning journey.
 */
public record ProgressMetricsResponse(
        Long userId,
        String username,
        int activeCourses,
        int completedCourses,
        int totalExercisesCompleted,
        double overallCompletionPercentage,
        double averageScore,
        int totalTimeSpentMinutes,
        int studyStreakDays,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate lastActivityDate,

        List<CourseProgressMetrics> courseMetrics,
        Map<String, Double> skillProficiencyMap,
        WeeklyActivitySummary weeklyActivity,
        LearningPaceMetrics learningPace
) {}

