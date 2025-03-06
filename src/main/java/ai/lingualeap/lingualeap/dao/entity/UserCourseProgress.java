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
@Table(name = "user_course_progress")
public class UserCourseProgress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "completed_modules")
    private Integer completedModules = 0;

    @Column(name = "total_modules")
    private Integer totalModules = 0;

    @Column(name = "completed_lessons")
    private Integer completedLessons = 0;

    @Column(name = "total_lessons")
    private Integer totalLessons = 0;

    @Column(name = "completed_exercises")
    private Integer completedExercises = 0;

    @Column(name = "total_exercises")
    private Integer totalExercises = 0;

    @Column(name = "average_score")
    private Double averageScore = 0.0;

    @Column(name = "completion_percentage")
    private Double completionPercentage = 0.0;

    @Column(name = "total_time_spent")
    private Integer totalTimeSpent = 0;  // as a minute

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompletionStatus status = CompletionStatus.NOT_STARTED;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "last_activity_at")
    private LocalDateTime lastActivityAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    public void lessonCompleted() {
        this.completedLessons++;
        updateCompletionPercentage();
        updateStatus();
    }

    public void moduleCompleted() {
        this.completedModules++;
        updateCompletionPercentage();
        updateStatus();
    }

    public void exerciseCompleted(Double score) {
        this.completedExercises++;
        updateAverageScore(score);
        updateCompletionPercentage();
        updateStatus();
    }

    private void updateCompletionPercentage() {
        if (this.totalLessons > 0) {
            this.completionPercentage = (double) this.completedLessons / this.totalLessons * 100;
        }
    }

    private void updateAverageScore(Double newScore) {
        if (this.completedExercises == 1) {
            this.averageScore = newScore;
        } else if (this.completedExercises > 1) {
            double totalScore = this.averageScore * (this.completedExercises - 1);
            this.averageScore = (totalScore + newScore) / this.completedExercises;
        }
    }

    private void updateStatus() {
        if (this.completedLessons == 0) {
            this.status = CompletionStatus.NOT_STARTED;
        } else if (this.completedLessons == this.totalLessons) {
            this.status = CompletionStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
        } else {
            this.status = CompletionStatus.IN_PROGRESS;
        }
    }

    public void updateLastActivity() {
        this.lastActivityAt = LocalDateTime.now();
    }

    /**
     * Course start date
     */
    public void start() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
            this.status = CompletionStatus.IN_PROGRESS;
        }
    }
}
