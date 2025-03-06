package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.ExerciseProgress;
import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.model.request.ExerciseProgressUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseProgressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExerciseProgressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "exercise", source = "exercise")
    @Mapping(target = "attemptCount", constant = "0")
    @Mapping(target = "lastAttemptAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "status", source = "request.status")
    @Mapping(target = "score", source = "request.score")
    @Mapping(target = "timeSpent", source = "request.timeSpent")
    @Mapping(target = "answer", source = "request.answer")
    @Mapping(target = "feedback", source = "request.feedback")
    ExerciseProgress toEntity(ExerciseProgressUpdateRequest request, User user, ai.lingualeap.lingualeap.dao.entity.Exercise exercise);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    ExerciseProgressResponse toResponse(ExerciseProgress exerciseProgress);

    List<ExerciseProgressResponse> toResponseList(List<ExerciseProgress> exerciseProgressList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "exercise", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ExerciseProgressUpdateRequest request, @MappingTarget ExerciseProgress exerciseProgress);
}
