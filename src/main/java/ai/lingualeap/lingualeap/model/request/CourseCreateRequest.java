package ai.lingualeap.lingualeap.model.request;

import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CourseCreateRequest(
        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotBlank(message = "Target language is required")
        String targetLanguage,

        @NotBlank(message = "Source language is required")
        String sourceLanguage,

        @NotNull(message = "Level is required")
        CourseLevel level
) {}
