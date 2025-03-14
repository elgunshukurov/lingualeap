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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(name = "difficulty_level")
    private Integer difficultyLevel = 1; // 1-easy, 2-medium, 3-hard

    @Column(name = "requires_audio")
    private Boolean requiresAudio = false;

    @Column(name = "requires_speaking")
    private Boolean requiresSpeaking = false;

    @Column(name = "auto_gradable")
    private Boolean autoGradable = true;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Column(name = "hint_available")
    private Boolean hintAvailable = false;

    @Column(name = "hint_text", columnDefinition = "TEXT")
    private String hintText;

    @Column(name = "is_template")
    private Boolean isTemplate = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @ManyToMany
    @JoinTable(
            name = "exercise_tags",
            joinColumns = @JoinColumn(name = "exercise_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "exercise")
    private List<ExerciseOption> options = new ArrayList<>();

    // Helper methods

    public void addTag(Tag tag) {
        this.tags.add(tag);
        tag.getExercises().add(this);
    }

    public void removeTag(Tag tag) {
        this.tags.remove(tag);
        tag.getExercises().remove(this);
    }

    public void addOption(ExerciseOption option) {
        this.options.add(option);
        option.setExercise(this);
    }

    public void removeOption(ExerciseOption option) {
        this.options.remove(option);
        option.setExercise(null);
    }

    /**
     * Checks if this exercise is suitable for the provided exercise type
     * @return true if the exercise has the required data for its type
     */
    public boolean isValidForType() {
        switch (this.type) {
            case MULTIPLE_CHOICE:
                return !this.options.isEmpty();
            case FILL_IN_BLANK:
                return this.correctAnswer != null && !this.correctAnswer.isEmpty();
            case LISTENING:
                return this.requiresAudio;
            case SPEAKING:
                return this.requiresSpeaking;
            case MATCHING:
                return !this.options.isEmpty();
            case TRANSLATION:
                return this.correctAnswer != null && !this.correctAnswer.isEmpty();
            case WRITING:
                return true; // Writing exercises are manually graded
            default:
                return true;
        }
    }
}
