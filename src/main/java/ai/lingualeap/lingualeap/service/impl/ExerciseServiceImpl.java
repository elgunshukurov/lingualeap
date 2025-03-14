package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.Exercise;
import ai.lingualeap.lingualeap.dao.entity.ExerciseOption;
import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.Tag;
import ai.lingualeap.lingualeap.dao.repository.ExerciseOptionRepository;
import ai.lingualeap.lingualeap.dao.repository.ExerciseRepository;
import ai.lingualeap.lingualeap.dao.repository.LessonRepository;
import ai.lingualeap.lingualeap.dao.repository.TagRepository;
import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import ai.lingualeap.lingualeap.model.request.ExerciseCreateRequest;
import ai.lingualeap.lingualeap.model.request.ExerciseOptionRequest;
import ai.lingualeap.lingualeap.model.request.ExerciseUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseResponse;
import ai.lingualeap.lingualeap.service.ExerciseService;
import ai.lingualeap.lingualeap.service.mapper.ExerciseMapper;
import ai.lingualeap.lingualeap.service.mapper.ExerciseOptionMapper;
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
public class ExerciseServiceImpl implements ExerciseService {

    private static final String EXERCISE_NOT_FOUND = "Exercise not found with id: ";
    private static final String LESSON_NOT_FOUND = "Lesson not found with id: ";
    private static final String TAG_NOT_FOUND = "Tag not found with id: ";
    private static final String SEQUENCE_EXISTS = "Exercise with sequence %d already exists in lesson";

    private final ExerciseRepository exerciseRepository;
    private final LessonRepository lessonRepository;
    private final TagRepository tagRepository;
    private final ExerciseOptionRepository exerciseOptionRepository;
    private final ExerciseMapper exerciseMapper;
    private final ExerciseOptionMapper exerciseOptionMapper;

    @Override
    @Transactional
    public ExerciseResponse createExercise(ExerciseCreateRequest request) {

        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new EntityNotFoundException(LESSON_NOT_FOUND + request.lessonId()));

        // Check if sequence already exists
        if (request.sequence() != null &&
                exerciseRepository.existsByLessonIdAndSequence(lesson.getId(), request.sequence())) {
            throw new IllegalArgumentException(String.format(SEQUENCE_EXISTS, request.sequence()));
        }

        // Collect tags if provided
        Set<Tag> tags = new HashSet<>();
        if (request.tagIds() != null && !request.tagIds().isEmpty()) {
            tags = request.tagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new EntityNotFoundException(TAG_NOT_FOUND + tagId)))
                    .collect(Collectors.toSet());
        }

        // Create exercise entity
        Exercise exercise = exerciseMapper.mapExerciseWithTags(request, lesson, tags);

        // Save the exercise first to get an ID
        exercise = exerciseRepository.save(exercise);

        // Handle options for multiple choice or matching exercises
        if (request.options() != null && !request.options().isEmpty()) {
            List<ExerciseOption> options = createOptionsFromRequest(request.options(), exercise);
            exerciseOptionRepository.saveAll(options);
        }

        log.info("Exercise created with id: {}", exercise.getId());
        return exerciseMapper.toResponse(exercise);
    }

    @Override
    @Transactional
    public ExerciseResponse updateExercise(Long id, ExerciseUpdateRequest request) {
        log.debug("Updating exercise with id: {}", id);

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EXERCISE_NOT_FOUND + id));

        // Update exercise properties
        exerciseMapper.updateEntityFromRequest(request, exercise);

        // Update tags if provided
        if (request.tagIds() != null) {
            Set<Tag> newTags = request.tagIds().stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new EntityNotFoundException(TAG_NOT_FOUND + tagId)))
                    .collect(Collectors.toSet());

            exercise.getTags().clear();
            exercise.getTags().addAll(newTags);
        }

        // Update options if provided
        if (request.options() != null) {
            // Remove existing options
            exerciseOptionRepository.deleteByExerciseId(id);

            // Create new options
            if (!request.options().isEmpty()) {
                List<ExerciseOption> newOptions = createOptionsFromRequest(request.options(), exercise);
                exerciseOptionRepository.saveAll(newOptions);
            }
        }

        exercise = exerciseRepository.save(exercise);
        log.info("Exercise updated with id: {}", id);

        return exerciseMapper.toResponse(exercise);
    }

    @Override
    public ExerciseResponse getExerciseById(Long id) {
        log.debug("Getting exercise by id: {}", id);

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EXERCISE_NOT_FOUND + id));

        return exerciseMapper.toResponse(exercise);
    }

    @Override
    public List<ExerciseResponse> getExercisesByLessonId(Long lessonId) {
        log.debug("Getting exercises by lesson id: {}", lessonId);

        if (!lessonRepository.existsById(lessonId)) {
            throw new EntityNotFoundException(LESSON_NOT_FOUND + lessonId);
        }

        List<Exercise> exercises = exerciseRepository.findByLessonIdOrderBySequenceAsc(lessonId);
        return exerciseMapper.toResponseList(exercises);
    }

    @Override
    public Page<ExerciseResponse> searchExercises(
            Long lessonId,
            ExerciseType type,
            ExerciseStatus status,
            Integer difficultyLevel,
            Pageable pageable) {

        log.debug("Searching exercises with filters: lessonId={}, type={}, status={}, difficultyLevel={}",
                lessonId, type, status, difficultyLevel);

        Specification<Exercise> spec = Specification.where(null);

        if (lessonId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("lesson").get("id"), lessonId));
        }

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (difficultyLevel != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("difficultyLevel"), difficultyLevel));
        }

        return exerciseRepository.findAll(spec, pageable)
                .map(exerciseMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        log.debug("Deleting exercise with id: {}", id);

        if (!exerciseRepository.existsById(id)) {
            throw new EntityNotFoundException(EXERCISE_NOT_FOUND + id);
        }

        // Delete all options first
        exerciseOptionRepository.deleteByExerciseId(id);

        // Then delete the exercise
        exerciseRepository.deleteById(id);
        log.info("Exercise deleted with id: {}", id);
    }

    @Override
    @Transactional
    public ExerciseResponse updateExerciseStatus(Long id, ExerciseStatus status) {
        log.debug("Updating exercise status. Id: {}, new status: {}", id, status);

        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EXERCISE_NOT_FOUND + id));

        exercise.setStatus(status);
        exercise = exerciseRepository.save(exercise);

        log.info("Updated exercise status. Id: {}, status: {}", id, status);
        return exerciseMapper.toResponse(exercise);
    }

    @Override
    @Transactional
    public void reorderExercises(Long lessonId, Map<Long, Integer> exerciseSequences) {
        log.debug("Reordering exercises for lesson: {}", lessonId);

        if (!lessonRepository.existsById(lessonId)) {
            throw new EntityNotFoundException(LESSON_NOT_FOUND + lessonId);
        }

        // Get all exercises for this lesson
        List<Exercise> exercises = exerciseRepository.findByLessonIdOrderBySequenceAsc(lessonId);

        // Update sequence for each exercise
        exercises.forEach(exercise -> {
            Integer newSequence = exerciseSequences.get(exercise.getId());
            if (newSequence != null) {
                exercise.setSequence(newSequence);
            }
        });

        exerciseRepository.saveAll(exercises);
        log.info("Reordered exercises for lesson: {}", lessonId);
    }

    @Override
    public List<ExerciseResponse> getExercisesByTagId(Long tagId) {
        log.debug("Getting exercises by tag id: {}", tagId);

        if (!tagRepository.existsById(tagId)) {
            throw new EntityNotFoundException(TAG_NOT_FOUND + tagId);
        }

        List<Exercise> exercises = exerciseRepository.findByTagId(tagId);
        return exerciseMapper.toResponseList(exercises);
    }

    @Override
    public List<ExerciseResponse> getExerciseTemplates(ExerciseType type) {
        log.debug("Getting exercise templates by type: {}", type);

        List<Exercise> templates = exerciseRepository.findTemplatesByType(type);
        return exerciseMapper.toResponseList(templates);
    }

    @Override
    @Transactional
    public void validateExerciseData(Long exerciseId) {
        log.debug("Validating data for exercise: {}", exerciseId);

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new EntityNotFoundException(EXERCISE_NOT_FOUND + exerciseId));

        if (!exercise.isValidForType()) {
            throw new IllegalStateException("Exercise data is not valid for type: " + exercise.getType());
        }

        log.info("Exercise data validated successfully: {}", exerciseId);
    }

    // Helper methods
    private List<ExerciseOption> createOptionsFromRequest(List<ExerciseOptionRequest> optionRequests, Exercise exercise) {
        List<ExerciseOption> options = new ArrayList<>();

        for (ExerciseOptionRequest optionRequest : optionRequests) {
            ExerciseOption option = exerciseOptionMapper.toEntity(optionRequest, exercise);
            options.add(option);
        }

        return options;
    }
}
