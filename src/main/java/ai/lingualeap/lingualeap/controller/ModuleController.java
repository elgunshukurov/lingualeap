package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.model.request.ModuleCreateRequest;
import ai.lingualeap.lingualeap.model.response.ModuleResponse;
import ai.lingualeap.lingualeap.service.ModuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@Tag(name = "Module Management", description = "API endpoints for managing modules")
public class ModuleController {

    private final ModuleService moduleService;

    @Operation(summary = "Create a new module")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Module created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    public ResponseEntity<ModuleResponse> createModule(
            @Valid @RequestBody ModuleCreateRequest request) {
        log.debug("REST request to create Module: {}", request);
        return new ResponseEntity<>(moduleService.createModule(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Get module by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Module found"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponse> getModuleById(
            @Parameter(description = "Module ID") @PathVariable Long id) {
        log.debug("REST request to get Module: {}", id);
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @Operation(summary = "Get modules by course ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Modules found"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<ModuleResponse>> getModulesByCourseId(
            @Parameter(description = "Course ID") @PathVariable Long courseId) {
        log.debug("REST request to get Modules by Course: {}", courseId);
        return ResponseEntity.ok(moduleService.getModulesByCourseId(courseId));
    }

    @Operation(summary = "Delete a module")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Module deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Module not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteModule(
            @Parameter(description = "Module ID") @PathVariable Long id) {
        log.debug("REST request to delete Module: {}", id);
        moduleService.deleteModule(id);
        return ResponseEntity.noContent().build();
    }
}
