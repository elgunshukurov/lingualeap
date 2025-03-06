package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import ai.lingualeap.lingualeap.model.request.CourseCreateRequest;
import ai.lingualeap.lingualeap.model.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseCreateRequest request);

    CourseResponse getCourseById(Long id);

    List<CourseResponse> getAllCourses();

    Page<CourseResponse> searchCourses(
            String targetLanguage,
            String sourceLanguage,
            CourseLevel level,
            Pageable pageable);

    void deleteCourse(Long id);
}
