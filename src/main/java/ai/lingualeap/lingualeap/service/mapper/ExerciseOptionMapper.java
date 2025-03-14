package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Exercise;
import ai.lingualeap.lingualeap.dao.entity.ExerciseOption;
import ai.lingualeap.lingualeap.model.request.ExerciseOptionRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseOptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExerciseOptionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", source = "exercise")
    @Mapping(target = "content", source = "request.content")
    @Mapping(target = "sequence", source = "request.sequence")
    @Mapping(target = "isCorrect", source = "request.isCorrect")
    @Mapping(target = "feedbackText", source = "request.feedbackText")
    @Mapping(target = "matchKey", source = "request.matchKey")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    ExerciseOption toEntity(ExerciseOptionRequest request, Exercise exercise);

    ExerciseOptionResponse toResponse(ExerciseOption option);

    List<ExerciseOptionResponse> toResponseList(List<ExerciseOption> options);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntityFromRequest(ExerciseOptionRequest request, @MappingTarget ExerciseOption option);
}
