package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import ai.lingualeap.lingualeap.model.request.CourseCreateRequest;
import ai.lingualeap.lingualeap.model.response.CourseResponse;
import ai.lingualeap.lingualeap.service.CourseService;
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
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Tag(name = "Course Management", description = "API endpoints for managing courses")
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Create a new course")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> createCourse(
            @Valid @RequestBody CourseCreateRequest request) {
        log.debug("REST request to create Course: {}", request);
        return new ResponseEntity<>(courseService.createCourse(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get course by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        log.debug("REST request to get Course: {}", id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(summary = "Get all courses")
    @ApiResponse(responseCode = "200", description = "Courses found")
    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        log.debug("REST request to get all Courses");
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @Operation(summary = "Search courses with filters")
    @ApiResponse(responseCode = "200", description = "Search completed")
    @GetMapping("/search")
    public ResponseEntity<Page<CourseResponse>> searchCourses(
            @Parameter(description = "Target language")
            @RequestParam(required = false) String targetLanguage,

            @Parameter(description = "Source language")
            @RequestParam(required = false) String sourceLanguage,

            @Parameter(description = "Course level")
            @RequestParam(required = false) CourseLevel level,

            @Parameter(description = "Pagination parameters")
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search Courses");
        return ResponseEntity.ok(courseService.searchCourses(targetLanguage, sourceLanguage, level, pageable));
    }

    @Operation(summary = "Delete a course")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID") @PathVariable Long id) {
        log.debug("REST request to delete Course: {}", id);
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}
