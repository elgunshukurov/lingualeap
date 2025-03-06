package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.LessonProgress;
import ai.lingualeap.lingualeap.model.response.LessonProgressResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {LessonMapper.class})
public interface LessonProgressMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalAttempts", constant = "0")
    @Mapping(target = "successfulAttempts", constant = "0")
    @Mapping(target = "averageScore", constant = "0.0")
    @Mapping(target = "bestScore", constant = "0.0")
    @Mapping(target = "totalTimeSpent", constant = "0")
    @Mapping(target = "lastAttemptAt", ignore = true)
    @Mapping(target = "completionDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", constant = "NOT_STARTED")
    @Mapping(target = "isDeleted", constant = "false")
    @Mapping(target = "version", ignore = true)
    LessonProgress toEntity(Lesson lesson);

    LessonProgressResponse toResponse(LessonProgress lessonProgress);

    List<LessonProgressResponse> toResponseList(List<LessonProgress> lessonProgressList);
}
