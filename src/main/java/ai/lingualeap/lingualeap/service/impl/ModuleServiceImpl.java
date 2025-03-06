package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.entity.Module;
import ai.lingualeap.lingualeap.dao.repository.CourseRepository;
import ai.lingualeap.lingualeap.dao.repository.ModuleRepository;
import ai.lingualeap.lingualeap.model.request.ModuleCreateRequest;
import ai.lingualeap.lingualeap.model.response.ModuleResponse;
import ai.lingualeap.lingualeap.service.ModuleService;
import ai.lingualeap.lingualeap.service.mapper.ModuleMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModuleServiceImpl implements ModuleService {

    public static final String MODULE_NOT_FOUND_WITH_ID = "Module not found with id: ";
    public static final String COURSE_NOT_FOUND_WITH_ID = "Course not found with id: ";
    private final ModuleRepository moduleRepository;
    private final CourseRepository courseRepository;
    private final ModuleMapper moduleMapper;

    @Override
    @Transactional
    public ModuleResponse createModule(ModuleCreateRequest request) {
        log.debug("Creating new module with title: {}", request.title());

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND_WITH_ID + request.courseId()));

        if (moduleRepository.existsByCourseIdAndSequence(course.getId(), request.sequence())) {
            throw new IllegalArgumentException("Module with sequence " + request.sequence() + " already exists in course");
        }

        Module module = moduleMapper.toEntity(request);
        module.setCourse(course);

        module = moduleRepository.save(module);
        log.info("Module created with id: {}", module.getId());

        return moduleMapper.toResponse(module);
    }

    @Override
    public ModuleResponse getModuleById(Long id) {
        log.debug("Getting module by id: {}", id);

        return moduleRepository.findById(id)
                .map(moduleMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(MODULE_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public List<ModuleResponse> getModulesByCourseId(Long courseId) {
        log.debug("Getting modules by course id: {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new EntityNotFoundException(COURSE_NOT_FOUND_WITH_ID + courseId);
        }

        List<Module> modules = moduleRepository.findByCourseIdOrderBySequenceAsc(courseId);
        return moduleMapper.toResponseList(modules);
    }

    @Override
    @Transactional
    public void deleteModule(Long id) {
        log.debug("Deleting module with id: {}", id);

        if (!moduleRepository.existsById(id)) {
            throw new EntityNotFoundException(MODULE_NOT_FOUND_WITH_ID + id);
        }

        moduleRepository.deleteById(id);
        log.info("Module deleted with id: {}", id);
    }
}
