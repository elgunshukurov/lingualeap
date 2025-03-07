package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.response.LearningRecommendationResponse;
import ai.lingualeap.lingualeap.model.response.ProgressMetricsResponse;
import ai.lingualeap.lingualeap.model.response.SkillStrengthResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * Service for analyzing learning progress and providing insights/recommendations.
 */
public interface ProgressAnalyticsService {

    /**
     * Get comprehensive progress metrics for a user.
     *
     * @param userId The user ID
     * @return Progress metrics and statistics
     */
    ProgressMetricsResponse getUserProgressMetrics(Long userId);

    /**
     * Analyze user strengths and weaknesses based on performance data.
     *
     * @param userId The user ID
     * @return List of skills with strength assessment
     */
    List<SkillStrengthResponse> analyzeUserSkillStrengths(Long userId);

    /**
     * Generate personalized learning recommendations based on user progress.
     *
     * @param userId The user ID
     * @return List of learning recommendations
     */
    List<LearningRecommendationResponse> generateLearningRecommendations(Long userId);

    /**
     * Get user study streak information (consecutive days with learning activity).
     *
     * @param userId The user ID
     * @return Number of consecutive days with activity
     */
    int getUserStudyStreak(Long userId);

    /**
     * Calculate daily activity summary for a date range.
     *
     * @param userId The user ID
     * @param startDate The start date
     * @param endDate The end date
     * @return Daily activity data within date range
     */
    List<DailyActivityResponse> getDailyActivitySummary(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Get estimated completion date for a course based on current progress and study patterns.
     *
     * @param userId The user ID
     * @param courseId The course ID
     * @return Estimated completion date
     */
    LocalDate getEstimatedCourseCompletionDate(Long userId, Long courseId);

    /**
     * Find learning patterns and create optimized schedule recommendations.
     *
     * @param userId The user ID
     * @return Learning schedule recommendations
     */
    LearningScheduleResponse generateOptimalLearningSchedule(Long userId);

    /**
     * Daily activity data including exercises completed, time spent, and score.
     */
    record DailyActivityResponse(
            LocalDate date,
            int exercisesCompleted,
            int timeSpentMinutes,
            double averageScore
    ) {}

    /**
     * Learning schedule recommendation with optimal study times.
     */
    record LearningScheduleResponse(
            List<ScheduleSlot> recommendedSlots,
            int optimalDailyMinutes,
            int optimalSessionsPerWeek,
            boolean weekendsRecommended
    ) {}

    /**
     * Recommended learning time slot.
     */
    record ScheduleSlot(
            String dayOfWeek,
            String startTime,
            String endTime,
            String focusArea
    ) {}
}
