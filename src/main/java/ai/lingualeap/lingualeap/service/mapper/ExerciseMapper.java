package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Exercise;
import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.Tag;
import ai.lingualeap.lingualeap.model.request.ExerciseCreateRequest;
import ai.lingualeap.lingualeap.model.request.ExerciseUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {ExerciseOptionMapper.class, TagMapper.class})
public interface ExerciseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "correctAnswer", source = "request.correctAnswer")
    @Mapping(target = "answerExplanation", source = "request.answerExplanation")
    @Mapping(target = "points", source = "request.points")
    @Mapping(target = "timeLimit", source = "request.timeLimit")
    @Mapping(target = "type", source = "request.type")
    @Mapping(target = "sequence", source = "request.sequence")
    @Mapping(target = "difficultyLevel", source = "request.difficultyLevel")
    @Mapping(target = "requiresAudio", source = "request.requiresAudio")
    @Mapping(target = "requiresSpeaking", source = "request.requiresSpeaking")
    @Mapping(target = "autoGradable", source = "request.autoGradable")
    @Mapping(target = "maxAttempts", source = "request.maxAttempts")
    @Mapping(target = "hintAvailable", source = "request.hintAvailable")
    @Mapping(target = "hintText", source = "request.hintText")
    @Mapping(target = "isTemplate", source = "request.isTemplate")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Exercise toEntity(ExerciseCreateRequest request);

    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "lessonTitle", source = "lesson.title")
    ExerciseResponse toResponse(Exercise exercise);

    List<ExerciseResponse> toResponseList(List<Exercise> exercises);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "options", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(ExerciseUpdateRequest request, @MappingTarget Exercise exercise);

    // Auxiliary methods for tag handling
    default Exercise mapExerciseWithTags(ExerciseCreateRequest request, Lesson lesson, Set<Tag> tags) {
        Exercise exercise = toEntity(request);
        exercise.setLesson(lesson);
        exercise.setTags(tags);
        return exercise;
    }
}
