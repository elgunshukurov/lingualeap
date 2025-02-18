package ai.lingualeap.lingualeap.model.response;

public record ModuleResponse(
        Long id,
        String title,
        String description,
        Integer sequence,
        CourseResponse course
) {
}
