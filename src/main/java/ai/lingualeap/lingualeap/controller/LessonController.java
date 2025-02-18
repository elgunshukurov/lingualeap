package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.request.LessonCreateRequest;
import ai.lingualeap.lingualeap.model.request.LessonUpdateRequest;
import ai.lingualeap.lingualeap.model.response.LessonResponse;
import ai.lingualeap.lingualeap.service.LessonService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lesson Management", description = "Endpoints for managing lessons")
public class LessonController {

    private final LessonService lessonService;

    @Operation(summary = "Create a new lesson")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Lesson created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> createLesson(
            @Valid @RequestBody LessonCreateRequest request
    ) {
        log.debug("REST request to create Lesson: {}", request);
        return new ResponseEntity<>(lessonService.createLesson(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing lesson")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> updateLesson(
            @Parameter(description = "Lesson ID") @PathVariable Long id,
            @Valid @RequestBody LessonUpdateRequest request
    ) {
        log.debug("REST request to update Lesson: {}", id);
        return ResponseEntity.ok(lessonService.updateLesson(id, request));
    }

    @Operation(summary = "Get lesson by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lesson found"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LessonResponse> getLesson(
            @Parameter(description = "Lesson ID") @PathVariable Long id
    ) {
        log.debug("REST request to get Lesson: {}", id);
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @Operation(summary = "Search lessons with filters")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search successful")
    })
    @GetMapping
    public ResponseEntity<Page<LessonResponse>> searchLessons(
            @Parameter(description = "Filter by lesson type")
            @RequestParam(required = false) LessonType type,

            @Parameter(description = "Filter by lesson level")
            @RequestParam(required = false) LessonLevel level,

            @Parameter(description = "Filter by lesson status")
            @RequestParam(required = false) LessonStatus status,

            @Parameter(description = "Filter by module ID")
            @RequestParam(required = false) Long moduleId,

            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable
    ) {
        log.debug("REST request to search Lessons");
        return ResponseEntity.ok(lessonService.searchLessons(type, level, status, moduleId, pageable));
    }

    @Operation(summary = "Delete a lesson")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lesson deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found"),
            @ApiResponse(responseCode = "400", description = "Lesson cannot be deleted")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLesson(
            @Parameter(description = "Lesson ID") @PathVariable Long id
    ) {
        log.debug("REST request to delete Lesson: {}", id);
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update lesson status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<LessonResponse> updateLessonStatus(
            @Parameter(description = "Lesson ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam LessonStatus status
    ) {
        log.debug("REST request to update Lesson status: {}", id);
        return ResponseEntity.ok(lessonService.updateLessonStatus(id, status));
    }

    @Operation(summary = "Reorder lessons in a module")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Lessons reordered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid sequence numbers")
    })
    @PutMapping("/module/{moduleId}/reorder")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> reorderLessons(
            @Parameter(description = "Module ID") @PathVariable Long moduleId,
            @RequestBody Map<Long, Integer> lessonSequences
    ) {
        log.debug("REST request to reorder lessons in module: {}", moduleId);
        lessonService.reorderLessons(moduleId, lessonSequences);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get lessons by module")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lessons retrieved successfully")
    })
    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<LessonResponse>> getLessonsByModule(
            @Parameter(description = "Module ID") @PathVariable Long moduleId
    ) {
        log.debug("REST request to get lessons by module: {}", moduleId);
        return ResponseEntity.ok(lessonService.getLessonsByModuleId(moduleId));
    }

    @Operation(summary = "Add prerequisite to lesson")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Prerequisite added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid prerequisite"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @PostMapping("/{id}/prerequisites/{prerequisiteId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> addPrerequisite(
            @Parameter(description = "Lesson ID") @PathVariable Long id,
            @Parameter(description = "Prerequisite lesson ID") @PathVariable Long prerequisiteId
    ) {
        log.debug("REST request to add prerequisite {} to lesson {}", prerequisiteId, id);
        lessonService.addPrerequisite(id, prerequisiteId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remove prerequisite from lesson")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Prerequisite removed successfully"),
            @ApiResponse(responseCode = "404", description = "Lesson not found")
    })
    @DeleteMapping("/{id}/prerequisites/{prerequisiteId}")
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<Void> removePrerequisite(
            @Parameter(description = "Lesson ID") @PathVariable Long id,
            @Parameter(description = "Prerequisite lesson ID") @PathVariable Long prerequisiteId
    ) {
        log.debug("REST request to remove prerequisite {} from lesson {}", prerequisiteId, id);
        lessonService.removePrerequisite(id, prerequisiteId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if lesson is completed by user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Check successful")
    })
    @GetMapping("/{id}/completed")
    public ResponseEntity<Boolean> isLessonCompleted(
            @Parameter(description = "Lesson ID") @PathVariable Long id,
            @Parameter(description = "User ID") @RequestParam Long userId
    ) {
        log.debug("REST request to check if lesson {} is completed by user {}", id, userId);
        return ResponseEntity.ok(lessonService.isLessonCompletedByUser(id, userId));
    }

    @Operation(summary = "Get available lessons for user in module")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available lessons retrieved successfully")
    })
    @GetMapping("/module/{moduleId}/available")
    public ResponseEntity<List<LessonResponse>> getAvailableLessons(
            @Parameter(description = "Module ID") @PathVariable Long moduleId,
            @Parameter(description = "User ID") @RequestParam Long userId
    ) {
        log.debug("REST request to get available lessons for user {} in module {}", userId, moduleId);
        return ResponseEntity.ok(lessonService.getAvailableLessonsForUser(userId, moduleId));
    }
}
