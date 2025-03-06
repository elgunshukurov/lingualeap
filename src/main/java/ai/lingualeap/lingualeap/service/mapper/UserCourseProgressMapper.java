package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.dao.entity.UserCourseProgress;
import ai.lingualeap.lingualeap.model.response.UserCourseProgressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class})
public interface UserCourseProgressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completedModules", constant = "0")
    @Mapping(target = "completedLessons", constant = "0")
    @Mapping(target = "completedExercises", constant = "0")
    @Mapping(target = "averageScore", constant = "0.0")
    @Mapping(target = "completionPercentage", constant = "0.0")
    @Mapping(target = "totalTimeSpent", constant = "0")
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "lastActivityAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", constant = "NOT_STARTED")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "totalModules", source = "totalModules")
    @Mapping(target = "totalLessons", source = "totalLessons")
    @Mapping(target = "totalExercises", source = "totalExercises")
    UserCourseProgress toEntity(User user, Course course, Integer totalModules, Integer totalLessons, Integer totalExercises);

    UserCourseProgressResponse toResponse(UserCourseProgress userCourseProgress);

    List<UserCourseProgressResponse> toResponseList(List<UserCourseProgress> userCourseProgressList);
}
