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
@Table(name = "lesson_progress")
public class LessonProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "total_attempts")
    private Integer totalAttempts = 0;

    @Column(name = "successful_attempts")
    private Integer successfulAttempts = 0;

    @Column(name = "average_score")
    private Double averageScore = 0.0;

    @Column(name = "best_score")
    private Double bestScore = 0.0;

    @Column(name = "total_time_spent")
    private Integer totalTimeSpent = 0;  // as a second

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompletionStatus status = CompletionStatus.NOT_STARTED;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void incrementTotalAttempts() {
        this.totalAttempts = this.totalAttempts + 1;
    }

    public void incrementSuccessfulAttempts() {
        this.successfulAttempts = this.successfulAttempts + 1;
    }

    public void updateAverageScore(Double newScore) {
        if (this.totalAttempts == 0) {
            this.averageScore = newScore;
        } else {
            double totalScore = this.averageScore * this.totalAttempts;
            this.averageScore = (totalScore + newScore) / (this.totalAttempts + 1);
        }

        if (newScore > this.bestScore) {
            this.bestScore = newScore;
        }
    }

    public void addTimeSpent(Integer secondsSpent) {
        this.totalTimeSpent = this.totalTimeSpent + secondsSpent;
    }

    public void markComplete() {
        this.status = CompletionStatus.COMPLETED;
        this.completionDate = LocalDateTime.now();
    }
}
