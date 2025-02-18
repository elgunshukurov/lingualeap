package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.model.request.LessonCreateRequest;
import ai.lingualeap.lingualeap.model.request.LessonUpdateRequest;
import ai.lingualeap.lingualeap.model.response.LessonResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "module", ignore = true)
    @Mapping(target = "prerequisites", ignore = true)
    @Mapping(target = "exercises", ignore = true)
    @Mapping(target = "status", constant = "DRAFT")
    Lesson toEntity(LessonCreateRequest request);

    LessonResponse toResponse(Lesson lesson);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(LessonUpdateRequest request, @MappingTarget Lesson lesson);

    List<LessonResponse> toResponseList(List<Lesson> lessons);
}
