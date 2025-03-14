package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.model.response.LearningRecommendationResponse;
import ai.lingualeap.lingualeap.model.response.ProgressMetricsResponse;
import ai.lingualeap.lingualeap.model.response.SkillStrengthResponse;
import ai.lingualeap.lingualeap.service.ProgressAnalyticsService;
import ai.lingualeap.lingualeap.service.ProgressAnalyticsService.DailyActivityResponse;
import ai.lingualeap.lingualeap.service.ProgressAnalyticsService.LearningScheduleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Progress Analytics", description = "API endpoints for progress analytics and insights")
public class ProgressAnalyticsController {

    private final ProgressAnalyticsService progressAnalyticsService;

    @Operation(summary = "Get user progress metrics")
    @ApiResponse(responseCode = "200", description = "Progress metrics retrieved successfully")
    @GetMapping("/users/{userId}/metrics")
    public ResponseEntity<ProgressMetricsResponse> getUserProgressMetrics(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.debug("REST request to get progress metrics for user {}", userId);
        return ResponseEntity.ok(progressAnalyticsService.getUserProgressMetrics(userId));
    }

    @Operation(summary = "Get user skill strengths")
    @ApiResponse(responseCode = "200", description = "Skill strengths retrieved successfully")
    @GetMapping("/users/{userId}/skills")
    public ResponseEntity<List<SkillStrengthResponse>> getUserSkillStrengths(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.debug("REST request to get skill strengths for user {}", userId);
        return ResponseEntity.ok(progressAnalyticsService.analyzeUserSkillStrengths(userId));
    }

    @Operation(summary = "Get learning recommendations")
    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully")
    @GetMapping("/users/{userId}/recommendations")
    public ResponseEntity<List<LearningRecommendationResponse>> getLearningRecommendations(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.debug("REST request to get learning recommendations for user {}", userId);
        return ResponseEntity.ok(progressAnalyticsService.generateLearningRecommendations(userId));
    }

    @Operation(summary = "Get user study streak")
    @ApiResponse(responseCode = "200", description = "Study streak retrieved successfully")
    @GetMapping("/users/{userId}/streak")
    public ResponseEntity<Integer> getUserStudyStreak(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.debug("REST request to get study streak for user {}", userId);
        return ResponseEntity.ok(progressAnalyticsService.getUserStudyStreak(userId));
    }

    @Operation(summary = "Get daily activity summary")
    @ApiResponse(responseCode = "200", description = "Daily activity summary retrieved successfully")
    @GetMapping("/users/{userId}/daily-activity")
    public ResponseEntity<List<DailyActivityResponse>> getDailyActivitySummary(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("REST request to get daily activity for user {} between {} and {}", userId, startDate, endDate);
        return ResponseEntity.ok(progressAnalyticsService.getDailyActivitySummary(userId, startDate, endDate));
    }

    @Operation(summary = "Get estimated course completion date")
    @ApiResponse(responseCode = "200", description = "Estimated completion date retrieved successfully")
    @GetMapping("/users/{userId}/courses/{courseId}/estimated-completion")
    public ResponseEntity<LocalDate> getEstimatedCourseCompletionDate(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.debug("REST request to get estimated completion date for user {} and course {}", userId, courseId);
        return ResponseEntity.ok(progressAnalyticsService.getEstimatedCourseCompletionDate(userId, courseId));
    }

    @Operation(summary = "Get optimal learning schedule")
    @ApiResponse(responseCode = "200", description = "Learning schedule retrieved successfully")
    @GetMapping("/users/{userId}/learning-schedule")
    public ResponseEntity<LearningScheduleResponse> getOptimalLearningSchedule(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.debug("REST request to get optimal learning schedule for user {}", userId);
        return ResponseEntity.ok(progressAnalyticsService.generateOptimalLearningSchedule(userId));
    }
}
