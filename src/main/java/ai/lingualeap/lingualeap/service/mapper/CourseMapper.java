package ai.lingualeap.lingualeap.service.mapper;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.model.request.CourseCreateRequest;
import ai.lingualeap.lingualeap.model.response.CourseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CourseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "modules", ignore = true)
    Course toEntity(CourseCreateRequest request);

    CourseResponse toResponse(Course course);

    List<CourseResponse> toResponseList(List<Course> courses);
}
