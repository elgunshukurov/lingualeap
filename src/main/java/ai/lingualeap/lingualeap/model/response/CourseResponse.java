package ai.lingualeap.lingualeap.model.response;

import ai.lingualeap.lingualeap.model.enums.CourseLevel;

public record CourseResponse(
        Long id,
        String title,
        String description,
        String targetLanguage,
        String sourceLanguage,
        CourseLevel level
) {
}
