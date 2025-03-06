package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.request.ExerciseProgressUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseProgressResponse;
import ai.lingualeap.lingualeap.model.response.LessonProgressResponse;
import ai.lingualeap.lingualeap.model.response.UserCourseProgressResponse;
import ai.lingualeap.lingualeap.service.ProgressService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "Progress Tracking", description = "API endpoints for progress tracking")
public class ProgressController {

    private final ProgressService progressService;

    @Operation(summary = "Get user's exercise progress by ID")
    @ApiResponse(responseCode = "200", description = "Progress found")
    @GetMapping("/exercises/user/{userId}")
    public ResponseEntity<List<ExerciseProgressResponse>> getExerciseProgressByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.debug("REST request to get exercise progress for user: {}", userId);
        return ResponseEntity.ok(progressService.getExerciseProgressByUserId(userId));
    }

    @Operation(summary = "Get specific exercise progress for user")
    @ApiResponse(responseCode = "200", description = "Progress found")
    @GetMapping("/exercises/user/{userId}/exercise/{exerciseId}")
    public ResponseEntity<ExerciseProgressResponse> getExerciseProgressByUserIdAndExerciseId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Exercise ID") @PathVariable Long exerciseId) {
        log.debug("REST request to get exercise progress for user: {} and exercise: {}", userId, exerciseId);
        return ResponseEntity.ok(progressService.getExerciseProgressByUserIdAndExerciseId(userId, exerciseId));
    }

    @Operation(summary = "Update exercise progress")
    @ApiResponse(responseCode = "200", description = "Progress updated")
    @PostMapping("/exercises/update")
    public ResponseEntity<ExerciseProgressResponse> updateExerciseProgress(
            @RequestBody ExerciseProgressUpdateRequest request) {
        log.debug("REST request to update exercise progress: {}", request);
        return ResponseEntity.ok(progressService.updateExerciseProgress(request));
    }

    @Operation(summary = "Get lesson progress by ID")
    @ApiResponse(responseCode = "200", description = "Progress found")
    @GetMapping("/lessons/{lessonProgressId}")
    public ResponseEntity<LessonProgressResponse> getLessonProgressById(
            @Parameter(description = "Lesson Progress ID") @PathVariable Long lessonProgressId) {
        log.debug("REST request to get lesson progress by id: {}", lessonProgressId);
        return ResponseEntity.ok(progressService.getLessonProgressById(lessonProgressId));
    }

    @Operation(summary = "Get all exercises progress for lesson and user")
    @ApiResponse(responseCode = "200", description = "Progress found")
    @GetMapping("/exercises/user/{userId}/lesson/{lessonId}")
    public ResponseEntity<List<ExerciseProgressResponse>> getExerciseProgressByUserIdAndLessonId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Lesson ID") @PathVariable Long lessonId) {
        log.debug("REST request to get exercise progress for user: {} and lesson: {}", userId, lessonId);
        return ResponseEntity.ok(progressService.getExerciseProgressByUserIdAndLessonId(userId, lessonId));
    }

    @Operation(summary = "Get course progress for user")
    @ApiResponse(responseCode = "200", description = "Progress found")
    @GetMapping("/courses/user/{userId}/course/{courseId}")
    public ResponseEntity<UserCourseProgressResponse> getUserCourseProgressByUserIdAndCourseId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.debug("REST request to get course progress for user: {} and course: {}", userId, courseId);
        return ResponseEntity.ok(progressService.getUserCourseProgressByUserIdAndCourseId(userId, courseId));
    }

    @Operation(summary = "Get all course progress for user")
    @ApiResponse(responseCode = "200", description = "Progress found")
    @GetMapping("/courses/user/{userId}")
    public ResponseEntity<List<UserCourseProgressResponse>> getUserCourseProgressByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        log.debug("REST request to get all course progress for user: {}", userId);
        return ResponseEntity.ok(progressService.getUserCourseProgressByUserId(userId));
    }

    @Operation(summary = "Get course progress by status")
    @ApiResponse(responseCode = "200", description = "Progress found")
    @GetMapping("/courses/user/{userId}/status")
    public ResponseEntity<List<UserCourseProgressResponse>> getUserCourseProgressByUserIdAndStatus(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Status") @RequestParam CompletionStatus status) {
        log.debug("REST request to get course progress for user: {} and status: {}", userId, status);
        return ResponseEntity.ok(progressService.getUserCourseProgressByUserIdAndStatus(userId, status));
    }

    @Operation(summary = "Enroll user to course")
    @ApiResponse(responseCode = "200", description = "User enrolled")
    @PostMapping("/courses/enroll/user/{userId}/course/{courseId}")
    public ResponseEntity<UserCourseProgressResponse> enrollUserToCourse(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.debug("REST request to enroll user: {} to course: {}", userId, courseId);
        return ResponseEntity.ok(progressService.enrollUserToCourse(userId, courseId));
    }

    @Operation(summary = "Get user activity summary")
    @ApiResponse(responseCode = "200", description = "Activity summary found")
    @GetMapping("/user/{userId}/activity")
    public ResponseEntity<List<ExerciseProgressResponse>> getUserActivitySummary(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.debug("REST request to get activity summary for user: {} between {} and {}", userId, startDate, endDate);
        return ResponseEntity.ok(progressService.getUserActivitySummary(userId, startDate, endDate));
    }

    @Operation(summary = "Calculate and update user course progress")
    @ApiResponse(responseCode = "200", description = "Progress calculated and updated")
    @PostMapping("/courses/calculate/user/{userId}/course/{courseId}")
    public ResponseEntity<UserCourseProgressResponse> calculateAndUpdateUserCourseProgress(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.debug("REST request to calculate course progress for user: {} and course: {}", userId, courseId);
        return ResponseEntity.ok(progressService.calculateAndUpdateUserCourseProgress(userId, courseId));
    }
}
