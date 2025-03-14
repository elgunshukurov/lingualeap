package ai.lingualeap.lingualeap.dao.repository;

import ai.lingualeap.lingualeap.dao.entity.ExerciseOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseOptionRepository extends JpaRepository<ExerciseOption, Long>, JpaSpecificationExecutor<ExerciseOption> {

    List<ExerciseOption> findByExerciseIdOrderBySequenceAsc(Long exerciseId);

    List<ExerciseOption> findByExerciseIdAndIsCorrect(Long exerciseId, Boolean isCorrect);

    void deleteByExerciseId(Long exerciseId);
}
