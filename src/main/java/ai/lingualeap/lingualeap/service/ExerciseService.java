package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import ai.lingualeap.lingualeap.model.request.ExerciseCreateRequest;
import ai.lingualeap.lingualeap.model.request.ExerciseUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ExerciseService {

    ExerciseResponse createExercise(ExerciseCreateRequest request);

    ExerciseResponse updateExercise(Long id, ExerciseUpdateRequest request);

    ExerciseResponse getExerciseById(Long id);

    List<ExerciseResponse> getExercisesByLessonId(Long lessonId);

    Page<ExerciseResponse> searchExercises(
            Long lessonId,
            ExerciseType type,
            ExerciseStatus status,
            Integer difficultyLevel,
            Pageable pageable);

    void deleteExercise(Long id);

    ExerciseResponse updateExerciseStatus(Long id, ExerciseStatus status);

    void reorderExercises(Long lessonId, Map<Long, Integer> exerciseSequences);

    List<ExerciseResponse> getExercisesByTagId(Long tagId);

    List<ExerciseResponse> getExerciseTemplates(ExerciseType type);

    void validateExerciseData(Long exerciseId);

}
