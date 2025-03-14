package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import ai.lingualeap.lingualeap.model.request.ExerciseCreateRequest;
import ai.lingualeap.lingualeap.model.request.ExerciseUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseResponse;
import ai.lingualeap.lingualeap.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise Management", description = "API endpoints for managing exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "Create a new exercise")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Exercise created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> createExercise(
            @Valid @RequestBody ExerciseCreateRequest request) {
        log.debug("REST request to create Exercise: {}", request.title());
        return new ResponseEntity<>(exerciseService.createExercise(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing exercise")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @Parameter(description = "Exercise ID") @PathVariable Long id,
            @Valid @RequestBody ExerciseUpdateRequest request) {
        log.debug("REST request to update Exercise: {}", id);
        return ResponseEntity.ok(exerciseService.updateExercise(id, request));
    }

    @Operation(summary = "Get exercise by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercise found"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getExerciseById(
            @Parameter(description = "Exercise ID") @PathVariable Long id) {
        log.debug("REST request to get Exercise: {}", id);
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @Operation(summary = "Get exercises by lesson ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercises found"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<ExerciseResponse>> getExercisesByLessonId(
            @Parameter(description = "Lesson ID") @PathVariable Long lessonId) {
        log.debug("REST request to get Exercises by Lesson: {}", lessonId);
        return ResponseEntity.ok(exerciseService.getExercisesByLessonId(lessonId));
    }

    @Operation(summary = "Search exercises with filters")
    @ApiResponse(responseCode = "200", description = "Search completed")
    @GetMapping("/search")
    public ResponseEntity<Page<ExerciseResponse>> searchExercises(
            @Parameter(description = "Lesson ID")
            @RequestParam(required = false) Long lessonId,

            @Parameter(description = "Exercise type")
            @RequestParam(required = false) ExerciseType type,

            @Parameter(description = "Exercise status")
            @RequestParam(required = false) ExerciseStatus status,

            @Parameter(description = "Exercise difficulty level (1-3)")
            @RequestParam(required = false) Integer difficultyLevel,

            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search Exercises");
        return ResponseEntity.ok(exerciseService.searchExercises(lessonId, type, status, difficultyLevel, pageable));
    }

    @Operation(summary = "Delete an exercise")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exercise deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExercise(
            @Parameter(description = "Exercise ID") @PathVariable Long id) {
        log.debug("REST request to delete Exercise: {}", id);
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update exercise status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Exercise not found")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> updateExerciseStatus(
            @Parameter(description = "Exercise ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam ExerciseStatus status) {
        log.debug("REST request to update Exercise status: {}", id);
        return ResponseEntity.ok(exerciseService.updateExerciseStatus(id, status));
    }

    @Operation(summary = "Reorder exercises in a lesson")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exercises reordered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sequence numbers")
    })
    @PutMapping("/lesson/{lessonId}/reorder")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> reorderExercises(
            @Parameter(description = "Lesson ID") @PathVariable Long lessonId,
            @RequestBody Map<Long, Integer> exerciseSequences) {
        log.debug("REST request to reorder exercises in lesson: {}", lessonId);
        exerciseService.reorderExercises(lessonId, exerciseSequences);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get exercises by tag")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Exercises found"),
            @ApiResponse(responseCode = "404", description = "Tag not found")
    })
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<ExerciseResponse>> getExercisesByTagId(
            @Parameter(description = "Tag ID") @PathVariable Long tagId) {
        log.debug("REST request to get Exercises by Tag: {}", tagId);
        return ResponseEntity.ok(exerciseService.getExercisesByTagId(tagId));
    }

    @Operation(summary = "Get exercise templates by type")
    @ApiResponse(responseCode = "200", description = "Templates found")
    @GetMapping("/templates")
    public ResponseEntity<List<ExerciseResponse>> getExerciseTemplates(
            @Parameter(description = "Exercise type")
            @RequestParam ExerciseType type) {
        log.debug("REST request to get Exercise templates for type: {}", type);
        return ResponseEntity.ok(exerciseService.getExerciseTemplates(type));
    }

    @Operation(summary = "Validate exercise data")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Exercise data is valid"),
            @ApiResponse(responseCode = "400", description = "Exercise data is invalid")
    })
    @PostMapping("/{id}/validate")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> validateExerciseData(
            @Parameter(description = "Exercise ID") @PathVariable Long id) {
        log.debug("REST request to validate Exercise data: {}", id);
        exerciseService.validateExerciseData(id);
        return ResponseEntity.noContent().build();
    }
}
