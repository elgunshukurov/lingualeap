package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.repository.CourseRepository;
import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import ai.lingualeap.lingualeap.model.request.CourseCreateRequest;
import ai.lingualeap.lingualeap.model.response.CourseResponse;
import ai.lingualeap.lingualeap.service.CourseService;
import ai.lingualeap.lingualeap.service.mapper.CourseMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    public static final String COURSE_NOT_FOUND_WITH_ID = "Course not found with id: ";
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {
        log.debug("Creating new course with title: {}", request.title());

        Course course = courseMapper.toEntity(request);
        course = courseRepository.save(course);

        log.info("Course created with id: {}", course.getId());
        return courseMapper.toResponse(course);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        log.debug("Getting course by id: {}", id);

        return courseRepository.findById(id)
                .map(courseMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public List<CourseResponse> getAllCourses() {
        log.debug("Getting all courses");

        List<Course> courses = courseRepository.findAll();
        return courseMapper.toResponseList(courses);
    }

    @Override
    public Page<CourseResponse> searchCourses(String targetLanguage, String sourceLanguage, CourseLevel level, Pageable pageable) {
        log.debug("Searching courses with filters: targetLanguage={}, sourceLanguage={}, level={}",
                targetLanguage, sourceLanguage, level);

        Specification<Course> spec = Specification.where(null);

        if (targetLanguage != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("targetLanguage"), targetLanguage));
        }

        if (sourceLanguage != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("sourceLanguage"), sourceLanguage));
        }

        if (level != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("level"), level));
        }

        return courseRepository.findAll(spec, pageable)
                .map(courseMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        log.debug("Deleting course with id: {}", id);

        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException(COURSE_NOT_FOUND_WITH_ID + id);
        }

        courseRepository.deleteById(id);
        log.info("Course deleted with id: {}", id);
    }
}
