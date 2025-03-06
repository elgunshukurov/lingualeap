package ai.lingualeap.lingualeap.dao.entity;

import ai.lingualeap.lingualeap.dao.entity.base.BaseEntity;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
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

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "exercise_progress")
public class ExerciseProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(name = "score")
    private Double score;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;

    @Column(name = "time_spent")
    private Integer timeSpent = 0;  // as a second

    @Column(name = "answer", columnDefinition = "TEXT")
    private String answer;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompletionStatus status = CompletionStatus.NOT_STARTED;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void incrementAttemptCount() {
        this.attemptCount = this.attemptCount + 1;
    }

    public void markComplete() {
        this.status = CompletionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markInProgress() {
        this.status = CompletionStatus.IN_PROGRESS;
    }

    public void markFailed() {
        this.status = CompletionStatus.FAILED;
    }

    public void updateLastAttempt() {
        this.lastAttemptAt = LocalDateTime.now();
    }
}
