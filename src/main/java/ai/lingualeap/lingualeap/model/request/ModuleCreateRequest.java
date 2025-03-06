package ai.lingualeap.lingualeap.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ModuleCreateRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull(message = "Sequence is required")
        Integer sequence,

        @NotNull(message = "Course ID is required")
        Long courseId
) {}
