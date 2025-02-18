package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.Module;
import ai.lingualeap.lingualeap.dao.repository.LessonRepository;
import ai.lingualeap.lingualeap.dao.repository.ModuleRepository;
import ai.lingualeap.lingualeap.dao.repository.UserProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.specification.LessonSpecifications;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.request.LessonCreateRequest;
import ai.lingualeap.lingualeap.model.request.LessonUpdateRequest;
import ai.lingualeap.lingualeap.model.response.LessonResponse;
import ai.lingualeap.lingualeap.service.LessonService;
import ai.lingualeap.lingualeap.service.mapper.LessonMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final UserProgressRepository userProgressRepository;
    private final LessonMapper lessonMapper;

    @Override
    @Transactional
    public LessonResponse createLesson(LessonCreateRequest request) {
        log.debug("Creating new lesson with title: {}", request.title());

        Module module = moduleRepository.findById(request.moduleId())
                .orElseThrow(() -> new EntityNotFoundException("Module not found with id: " + request.moduleId()));

        if (request.sequence() != null &&
                lessonRepository.existsByModuleIdAndSequence(module.getId(), request.sequence())) {
            throw new IllegalArgumentException("Sequence already exists in this module");
        }

        Lesson lesson = lessonMapper.toEntity(request);
        lesson.setModule(module);

        if (request.prerequisiteIds() != null && !request.prerequisiteIds().isEmpty()) {
            Set<Lesson> prerequisites = request.prerequisiteIds().stream()
                    .map(id -> lessonRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Prerequisite lesson not found with id: " + id)))
                    .collect(Collectors.toSet());
            lesson.setPrerequisites(prerequisites);
        }

        lesson = lessonRepository.save(lesson);
        log.info("Created new lesson with id: {}", lesson.getId());

        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long id, LessonUpdateRequest request) {
        log.debug("Updating lesson with id: {}", id);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));

        if (request.sequence() != null &&
                !request.sequence().equals(lesson.getSequence()) &&
                lessonRepository.existsByModuleIdAndSequence(lesson.getModule().getId(), request.sequence())) {
            throw new IllegalArgumentException("Sequence already exists in this module");
        }

        lessonMapper.updateEntityFromRequest(request, lesson);

        if (request.prerequisiteIds() != null) {
            Set<Lesson> prerequisites = request.prerequisiteIds().stream()
                    .map(prereqId -> lessonRepository.findById(prereqId)
                            .orElseThrow(() -> new EntityNotFoundException("Prerequisite lesson not found with id: " + prereqId)))
                    .collect(Collectors.toSet());
            lesson.setPrerequisites(prerequisites);
        }

        lesson = lessonRepository.save(lesson);
        log.info("Updated lesson with id: {}", lesson.getId());

        return lessonMapper.toResponse(lesson);
    }

    @Override
    public LessonResponse getLessonById(Long id) {
        log.debug("Getting lesson by id: {}", id);
        return lessonRepository.findById(id)
                .map(lessonMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));
    }

    @Override
    public Page<LessonResponse> searchLessons(LessonType type,
                                              LessonLevel level,
                                              LessonStatus status,
                                              Long moduleId,
                                              Pageable pageable) {
        log.debug("Searching lessons with type: {}, level: {}, status: {}, moduleId: {}",
                type, level, status, moduleId);

        Specification<Lesson> spec = Specification.where(null);

        if (type != null) {
            spec = spec.and(LessonSpecifications.hasType(type));
        }
        if (level != null) {
            spec = spec.and(LessonSpecifications.hasLevel(level));
        }
        if (status != null) {
            spec = spec.and(LessonSpecifications.hasStatus(status));
        }
        if (moduleId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("module").get("id"), moduleId));
        }

        return lessonRepository.findAll(spec, pageable)
                .map(lessonMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        log.debug("Deleting lesson with id: {}", id);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));

        List<Lesson> dependentLessons = lessonRepository.findByPrerequisitesContaining(lesson);
        if (!dependentLessons.isEmpty()) {
            throw new IllegalStateException("Lesson cannot be deleted as it is prerequisite for other lessons");
        }

        lessonRepository.delete(lesson);
        log.info("Deleted lesson with id: {}", id);
    }

    @Override
    @Transactional
    public LessonResponse updateLessonStatus(Long id, LessonStatus status) {
        log.debug("Updating lesson status. Id: {}, new status: {}", id, status);

        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + id));

        lesson.setStatus(status);
        lesson = lessonRepository.save(lesson);

        log.info("Updated lesson status. Id: {}, status: {}", id, status);
        return lessonMapper.toResponse(lesson);
    }

    @Override
    @Transactional
    public void reorderLessons(Long moduleId, Map<Long, Integer> lessonSequences) {
        log.debug("Reordering lessons for module: {}", moduleId);

        if (lessonSequences.values().size() != new HashSet<>(lessonSequences.values()).size()) {
            throw new IllegalArgumentException("Duplicate sequence numbers are not allowed");
        }

        List<Lesson> lessons = lessonRepository.findByModuleIdOrderBySequenceAsc(moduleId);
        lessons.forEach(lesson -> {
            Integer newSequence = lessonSequences.get(lesson.getId());
            if (newSequence != null) {
                lesson.setSequence(newSequence);
            }
        });

        lessonRepository.saveAll(lessons);
        log.info("Reordered lessons for module: {}", moduleId);
    }

    @Override
    public List<LessonResponse> getLessonsByModuleId(Long moduleId) {
        log.debug("Getting lessons by module id: {}", moduleId);
        return lessonMapper.toResponseList(
                lessonRepository.findByModuleIdOrderBySequenceAsc(moduleId)
        );
    }

    @Override
    @Transactional
    public void addPrerequisite(Long lessonId, Long prerequisiteId) {
        log.debug("Adding prerequisite {} to lesson {}", prerequisiteId, lessonId);

        if (lessonId.equals(prerequisiteId)) {
            throw new IllegalArgumentException("Lesson cannot be prerequisite of itself");
        }

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));

        Lesson prerequisite = lessonRepository.findById(prerequisiteId)
                .orElseThrow(() -> new EntityNotFoundException("Prerequisite lesson not found with id: " + prerequisiteId));

        if (isCircularDependency(prerequisite, lesson)) {
            throw new IllegalArgumentException("Adding this prerequisite would create a circular dependency");
        }

        lesson.getPrerequisites().add(prerequisite);
        lessonRepository.save(lesson);

        log.info("Added prerequisite {} to lesson {}", prerequisiteId, lessonId);
    }

    @Override
    @Transactional
    public void removePrerequisite(Long lessonId, Long prerequisiteId) {
        log.debug("Removing prerequisite {} from lesson {}", prerequisiteId, lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found with id: " + lessonId));

        lesson.getPrerequisites().removeIf(p -> p.getId().equals(prerequisiteId));
        lessonRepository.save(lesson);

        log.info("Removed prerequisite {} from lesson {}", prerequisiteId, lessonId);
    }

    @Override
    public boolean isLessonCompletedByUser(Long lessonId, Long userId) {
        return userProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .map(progress -> CompletionStatus.COMPLETED.equals(progress.getStatus()))
                .orElse(false);
    }

    @Override
    public List<LessonResponse> getAvailableLessonsForUser(Long userId, Long moduleId) {
        log.debug("Getting available lessons for user {} in module {}", userId, moduleId);

        List<Lesson> moduleLessons = lessonRepository.findByModuleIdOrderBySequenceAsc(moduleId);
        List<Lesson> availableLessons = new ArrayList<>();

        for (Lesson lesson : moduleLessons) {
            if (isLessonAvailableForUser(lesson, userId)) {
                availableLessons.add(lesson);
            }
        }

        return lessonMapper.toResponseList(availableLessons);
    }

    private boolean isLessonAvailableForUser(Lesson lesson, Long userId) {
        return lesson.getPrerequisites().stream()
                .allMatch(prerequisite -> isLessonCompletedByUser(prerequisite.getId(), userId));
    }

    private boolean isCircularDependency(Lesson potentialPrerequisite, Lesson targetLesson) {
        Set<Long> visited = new HashSet<>();
        return checkCircularDependency(potentialPrerequisite, targetLesson, visited);
    }

    private boolean checkCircularDependency(Lesson current, Lesson target, Set<Long> visited) {
        if (current.getId().equals(target.getId())) {
            return true;
        }

        if (!visited.add(current.getId())) {
            return false;
        }

        return current.getPrerequisites().stream()
                .anyMatch(prerequisite -> checkCircularDependency(prerequisite, target, visited));
    }
}
