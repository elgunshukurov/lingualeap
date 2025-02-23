package ai.lingualeap.lingualeap.dao.entity;

import ai.lingualeap.lingualeap.dao.entity.base.BaseEntity;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.vo.Score;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name = "user_progress")
public class UserProgress extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompletionStatus status = CompletionStatus.NOT_STARTED;

    @Embedded
    private Score score;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "last_attempt_at")
    private LocalDateTime lastAttemptAt;

    @Column(name = "attempt_count")
    private Integer attemptCount = 0;
}
