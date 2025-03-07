package ai.lingualeap.lingualeap.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

/**
 * Progress metrics for a specific course.
 */
public record CourseProgressMetrics(
        Long courseId,
        String courseTitle,
        double completionPercentage,
        int modulesCompleted,
        int totalModules,
        int lessonsCompleted,
        int totalLessons,
        int exercisesCompleted,
        int totalExercises,
        double averageScore,
        int timeSpentMinutes,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate estimatedCompletionDate
) {
}
