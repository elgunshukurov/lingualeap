package ai.lingualeap.lingualeap.dao.entity;

import ai.lingualeap.lingualeap.dao.entity.base.BaseEntity;
import ai.lingualeap.lingualeap.model.enums.ExerciseStatus;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "exercises")
public class Exercise extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(name = "answer_explanation", columnDefinition = "TEXT")
    private String answerExplanation;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseStatus status = ExerciseStatus.ACTIVE;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;
}
