package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.Exercise;
import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.repository.ExerciseRepository;
import ai.lingualeap.lingualeap.dao.repository.LessonRepository;
import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import ai.lingualeap.lingualeap.model.request.ExerciseCreateRequestCustom;
import ai.lingualeap.lingualeap.model.response.ExerciseResponse;
import ai.lingualeap.lingualeap.service.ExerciseService;
import ai.lingualeap.lingualeap.service.mapper.ExerciseMapper;
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
public class ExerciseServiceImpl implements ExerciseService {

    public static final String EXERCISE_NOT_FOUND_WITH_ID = "Exercise not found with id: ";
    public static final String LESSON_NOT_FOUND_WITH_ID = "Lesson not found with id: ";
    private final ExerciseRepository exerciseRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseMapper exerciseMapper;

    @Override
    @Transactional
    public ExerciseResponse createExercise(ExerciseCreateRequestCustom request) {
        log.debug("Creating new exercise with title: {}", request.title());

        Lesson lesson = lessonRepository.findById(request.lessonId())
                .orElseThrow(() -> new EntityNotFoundException(LESSON_NOT_FOUND_WITH_ID + request.lessonId()));

        if (request.sequence() != null &&
                exerciseRepository.existsByLessonIdAndSequence(lesson.getId(), request.sequence())) {
            throw new IllegalArgumentException("Exercise with sequence " + request.sequence() + " already exists in lesson");
        }

        Exercise exercise = exerciseMapper.toEntity(request);
        exercise.setLesson(lesson);

        exercise = exerciseRepository.save(exercise);
        log.info("Exercise created with id: {}", exercise.getId());

        return exerciseMapper.toResponse(exercise);
    }

    @Override
    public ExerciseResponse getExerciseById(Long id) {
        log.debug("Getting exercise by id: {}", id);

        return exerciseRepository.findById(id)
                .map(exerciseMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(EXERCISE_NOT_FOUND_WITH_ID + id));
    }

    @Override
    public List<ExerciseResponse> getExercisesByLessonId(Long lessonId) {
        log.debug("Getting exercises by lesson id: {}", lessonId);

        if (!lessonRepository.existsById(lessonId)) {
            throw new EntityNotFoundException(LESSON_NOT_FOUND_WITH_ID + lessonId);
        }

        List<Exercise> exercises = exerciseRepository.findByLessonIdOrderBySequenceAsc(lessonId);
        return exerciseMapper.toResponseList(exercises);
    }

    @Override
    public Page<ExerciseResponse> searchExercises(Long lessonId, ExerciseType type, ExerciseStatus status, Pageable pageable) {
        log.debug("Searching exercises with filters: lessonId={}, type={}, status={}",
                lessonId, type, status);

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

        return exerciseRepository.findAll(spec, pageable)
                .map(exerciseMapper::toResponse);
    }

    @Override
    @Transactional
    public void deleteExercise(Long id) {
        log.debug("Deleting exercise with id: {}", id);

        if (!exerciseRepository.existsById(id)) {
            throw new EntityNotFoundException(EXERCISE_NOT_FOUND_WITH_ID + id);
        }

        exerciseRepository.deleteById(id);
        log.info("Exercise deleted with id: {}", id);
    }
}
