package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import ai.lingualeap.lingualeap.model.request.ExerciseCreateRequestCustom;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @Valid @RequestBody ExerciseCreateRequestCustom request) {
        log.debug("REST request to create Exercise: {}", request);
        return new ResponseEntity<>(exerciseService.createExercise(request), HttpStatus.CREATED);
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

            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search Exercises");
        return ResponseEntity.ok(exerciseService.searchExercises(lessonId, type, status, pageable));
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
}
