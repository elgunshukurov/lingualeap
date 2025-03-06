package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import ai.lingualeap.lingualeap.model.request.ExerciseCreateRequestCustom;
import ai.lingualeap.lingualeap.model.response.ExerciseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExerciseService {

    ExerciseResponse createExercise(ExerciseCreateRequestCustom request);

    ExerciseResponse getExerciseById(Long id);

    List<ExerciseResponse> getExercisesByLessonId(Long lessonId);

    Page<ExerciseResponse> searchExercises(
            Long lessonId,
            ExerciseType type,
            ExerciseStatus status,
            Pageable pageable);

    void deleteExercise(Long id);
}
