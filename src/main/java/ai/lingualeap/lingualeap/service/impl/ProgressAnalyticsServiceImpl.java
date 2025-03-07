package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.config.CacheConfig.CacheNames;
import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.entity.ExerciseProgress;
import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.dao.entity.UserCourseProgress;
import ai.lingualeap.lingualeap.dao.repository.CourseRepository;
import ai.lingualeap.lingualeap.dao.repository.ExerciseProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.UserCourseProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.response.CourseProgressMetrics;
import ai.lingualeap.lingualeap.model.response.LearningRecommendationResponse;
import ai.lingualeap.lingualeap.model.response.ProgressMetricsResponse;
import ai.lingualeap.lingualeap.model.response.SkillStrengthResponse;
import ai.lingualeap.lingualeap.model.response.WeeklyActivitySummary;
import ai.lingualeap.lingualeap.model.response.LearningPaceMetrics;
import ai.lingualeap.lingualeap.service.ProgressAnalyticsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressAnalyticsServiceImpl implements ProgressAnalyticsService {

    private static final int DAYS_IN_WEEK = 7;
    private static final String FIELD_LAST_ATTEMPT_AT = "lastAttemptAt";
    private static final String FIELD_USER = "user";
    private static final String FIELD_ID = "id";
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String COURSE_NOT_FOUND = "Course not found with id: ";

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ExerciseProgressRepository exerciseProgressRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;

    @Override
    @Cacheable(value = CacheNames.USER_PROGRESS, key = "'metrics:' + #userId")
    public ProgressMetricsResponse getUserProgressMetrics(Long userId) {
        log.debug("Calculating progress metrics for user {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + userId));

        List<UserCourseProgress> courseProgresses = userCourseProgressRepository.findByUserId(userId);
        List<ExerciseProgress> exerciseProgresses = exerciseProgressRepository.findByUserId(userId);

        // Count courses by status
        int activeCourses = (int) courseProgresses.stream()
                .filter(cp -> CompletionStatus.IN_PROGRESS.equals(cp.getStatus()))
                .count();

        int completedCourses = (int) courseProgresses.stream()
                .filter(cp -> CompletionStatus.COMPLETED.equals(cp.getStatus()))
                .count();

        // Calculate total exercises completed
        int totalExercisesCompleted = (int) exerciseProgresses.stream()
                .filter(ep -> CompletionStatus.COMPLETED.equals(ep.getStatus()))
                .count();

        // Calculate overall completion percentage (weighted average of course completions)
        double overallCompletionPercentage = courseProgresses.stream()
                .mapToDouble(UserCourseProgress::getCompletionPercentage)
                .average()
                .orElse(0.0);

        // Calculate average score across all exercises
        double averageScore = exerciseProgresses.stream()
                .filter(ep -> ep.getScore() != null)
                .mapToDouble(ExerciseProgress::getScore)
                .average()
                .orElse(0.0);

        // Calculate total time spent in minutes
        int totalTimeSpentMinutes = exerciseProgresses.stream()
                .filter(ep -> ep.getTimeSpent() != null)
                .mapToInt(ExerciseProgress::getTimeSpent)
                .sum() / 60; // Convert seconds to minutes

        // Get streak days
        int studyStreakDays = getUserStudyStreak(userId);

        // Get last activity date
        LocalDate lastActivityDate = exerciseProgresses.stream()
                .filter(ep -> ep.getLastAttemptAt() != null)
                .map(ep -> ep.getLastAttemptAt().toLocalDate())
                .max(LocalDate::compareTo)
                .orElse(LocalDate.now());

        // Create course metrics
        List<CourseProgressMetrics> courseMetrics = courseProgresses.stream()
                .map(this::mapToCourseMetrics)
                .collect(Collectors.toList());

        // Create skill proficiency map (simplified for now)
        Map<String, Double> skillProficiencyMap = generateSkillProficiencyMap(exerciseProgresses);

        // Create weekly activity summary
        WeeklyActivitySummary weeklyActivity = createWeeklyActivitySummary(exerciseProgresses);

        // Create learning pace metrics
        LearningPaceMetrics learningPace = calculateLearningPaceMetrics(exerciseProgresses);

        return new ProgressMetricsResponse(
                userId,
                user.getUsername(),
                activeCourses,
                completedCourses,
                totalExercisesCompleted,
                overallCompletionPercentage,
                averageScore,
                totalTimeSpentMinutes,
                studyStreakDays,
                lastActivityDate,
                courseMetrics,
                skillProficiencyMap,
                weeklyActivity,
                learningPace
        );
    }

    @Override
    @Cacheable(value = CacheNames.USER_PROGRESS, key = "'skills:' + #userId")
    public List<SkillStrengthResponse> analyzeUserSkillStrengths(Long userId) {
        log.debug("Analyzing skill strengths for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        List<ExerciseProgress> userProgress = exerciseProgressRepository.findByUserId(userId);

        // Group exercises by their lesson's tags to identify skill categories
        Map<String, List<ExerciseProgress>> progressBySkill = new HashMap<>();

        // In a real implementation, we would categorize exercises by skill based on tags or other criteria
        // For this simplified implementation, we'll create sample skill categories
        progressBySkill.put("grammar", new ArrayList<>());
        progressBySkill.put("vocabulary", new ArrayList<>());
        progressBySkill.put("listening", new ArrayList<>());
        progressBySkill.put("reading", new ArrayList<>());

        // Distribute exercises to skill categories based on exercise type
        for (ExerciseProgress progress : userProgress) {
            String skillCategory;

            switch (progress.getExercise().getType()) {
                case MULTIPLE_CHOICE:
                case FILL_IN_BLANK:
                    skillCategory = "grammar";
                    break;
                case MATCHING:
                    skillCategory = "vocabulary";
                    break;
                case LISTENING:
                    skillCategory = "listening";
                    break;
                case TRANSLATION:
                case WRITING:
                    skillCategory = "reading";
                    break;
                default:
                    continue;
            }

            progressBySkill.get(skillCategory).add(progress);
        }

        // Calculate strength for each skill category
        List<SkillStrengthResponse> skillStrengths = new ArrayList<>();

        for (Map.Entry<String, List<ExerciseProgress>> entry : progressBySkill.entrySet()) {
            String skillCategory = entry.getKey();
            List<ExerciseProgress> skillProgress = entry.getValue();

            if (skillProgress.isEmpty()) {
                continue;
            }

            int exercisesCompleted = (int) skillProgress.stream()
                    .filter(p -> CompletionStatus.COMPLETED.equals(p.getStatus()))
                    .count();

            double averageScore = skillProgress.stream()
                    .filter(p -> p.getScore() != null)
                    .mapToDouble(ExerciseProgress::getScore)
                    .average()
                    .orElse(0);

            double strengthScore = calculateStrengthScore(exercisesCompleted, averageScore);
            String strengthLevel = determineStrengthLevel(strengthScore);

            // Get related lessons (would typically come from lesson data based on tags)
            List<String> relatedLessons = List.of("Basic " + skillCategory, "Advanced " + skillCategory);

            // Get recommended exercises (would be based on performance analysis)
            List<String> recommendedExercises = List.of(
                    skillCategory + " practice 1",
                    skillCategory + " practice 2"
            );

            skillStrengths.add(new SkillStrengthResponse(
                    skillCategory,
                    toTitleCase(skillCategory),
                    strengthScore,
                    strengthLevel,
                    exercisesCompleted,
                    averageScore,
                    relatedLessons,
                    recommendedExercises
            ));
        }

        return skillStrengths;
    }

    @Override
    @Cacheable(value = CacheNames.USER_PROGRESS, key = "'recommendations:' + #userId")
    public List<LearningRecommendationResponse> generateLearningRecommendations(Long userId) {
        log.debug("Generating learning recommendations for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        List<UserCourseProgress> courseProgresses = userCourseProgressRepository.findByUserId(userId);
        List<ExerciseProgress> exerciseProgresses = exerciseProgressRepository.findByUserId(userId);

        // Analyze activity patterns
        boolean hasRecentActivity = exerciseProgresses.stream()
                .filter(ep -> ep.getLastAttemptAt() != null)
                .anyMatch(ep -> ChronoUnit.DAYS.between(ep.getLastAttemptAt().toLocalDate(), LocalDate.now()) < 3);

        List<LearningRecommendationResponse> recommendations = new ArrayList<>();

        // 1. Recommend based on course progress
        for (UserCourseProgress courseProgress : courseProgresses) {
            if (CompletionStatus.IN_PROGRESS.equals(courseProgress.getStatus())) {
                Course course = courseProgress.getCourse();

                if (courseProgress.getCompletionPercentage() < 30.0) {
                    // For courses just started, recommend continuing
                    recommendations.add(new LearningRecommendationResponse(
                            "COURSE_CONTINUATION",
                            "Continue Your Course",
                            "You've made a good start on " + course.getTitle() + ". Keep the momentum going!",
                            0.9,
                            "Course Progression",
                            null,
                            null,
                            course.getId(),
                            course.getTitle(),
                            LocalDate.now().plusWeeks(1)
                    ));
                } else if (courseProgress.getCompletionPercentage() > 80.0) {
                    // For almost complete courses, recommend finishing
                    recommendations.add(new LearningRecommendationResponse(
                            "COURSE_COMPLETION",
                            "Finish Your Course",
                            "You're almost done with " + course.getTitle() + ". Just a little more to go!",
                            0.95,
                            "Course Completion",
                            null,
                            null,
                            course.getId(),
                            course.getTitle(),
                            LocalDate.now().plusWeeks(1)
                    ));
                }
            }
        }

        // 2. Recommend based on activity patterns
        if (!hasRecentActivity && !exerciseProgresses.isEmpty()) {
            recommendations.add(new LearningRecommendationResponse(
                    "ACTIVITY_REMINDER",
                    "Resume Your Learning",
                    "It's been a few days since your last practice. Keep up your learning streak!",
                    0.85,
                    "Learning Consistency",
                    null,
                    null,
                    null,
                    null,
                    LocalDate.now().plusDays(3)
            ));
        }

        // 3. Add skill-based recommendations (would be based on skill analysis)
        List<SkillStrengthResponse> skillStrengths = analyzeUserSkillStrengths(userId);

        // Find the weakest skill
        skillStrengths.stream()
                .min((s1, s2) -> Double.compare(s1.strengthScore(), s2.strengthScore()))
                .ifPresent(weakestSkill -> {
                    recommendations.add(new LearningRecommendationResponse(
                            "SKILL_IMPROVEMENT",
                            "Improve Your " + toTitleCase(weakestSkill.skillName()),
                            "Focus on improving your " + weakestSkill.skillName() + " to boost your overall proficiency.",
                            0.8,
                            weakestSkill.skillName(),
                            null,
                            null,
                            null,
                            null,
                            LocalDate.now().plusWeeks(2)
                    ));
                });

        return recommendations;
    }

    @Override
    public int getUserStudyStreak(Long userId) {
        log.debug("Calculating study streak for user {}", userId);

        List<ExerciseProgress> exerciseProgresses = exerciseProgressRepository.findAll(
                (root, query, cb) -> {
                    query.orderBy(cb.desc(root.get(FIELD_LAST_ATTEMPT_AT)));
                    return cb.equal(root.get(FIELD_USER).get(FIELD_ID), userId);
                }
        );

        if (exerciseProgresses.isEmpty()) {
            return 0;
        }

        // Get all activity dates sorted in descending order
        List<LocalDate> activityDates = exerciseProgresses.stream()
                .filter(ep -> ep.getLastAttemptAt() != null)
                .map(ep -> ep.getLastAttemptAt().toLocalDate())
                .distinct()
                .sorted((d1, d2) -> d2.compareTo(d1)) // Sort in descending order
                .collect(Collectors.toList());

        if (activityDates.isEmpty()) {
            return 0;
        }

        // Check if user has activity today
        LocalDate today = LocalDate.now();
        LocalDate lastActivityDate = activityDates.get(0);

        // If last activity is not from today or yesterday, streak is 0
        if (ChronoUnit.DAYS.between(lastActivityDate, today) > 1) {
            return 0;
        }

        // Count consecutive days with activity
        int streak = 1; // Start with 1 for today/yesterday
        LocalDate previousDate = lastActivityDate;

        for (int i = 1; i < activityDates.size(); i++) {
            LocalDate currentDate = activityDates.get(i);

            // If dates are consecutive
            if (ChronoUnit.DAYS.between(currentDate, previousDate) == 1) {
                streak++;
                previousDate = currentDate;
            } else {
                break;
            }
        }

        return streak;
    }

    @Override
    @Cacheable(value = CacheNames.USER_ACTIVITY, key = "'dailyActivity:' + #userId + ':' + #startDate + '-' + #endDate")
    public List<DailyActivityResponse> getDailyActivitySummary(Long userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting daily activity summary for user {} between {} and {}", userId, startDate, endDate);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay().minusNanos(1);

        // Get all exercise progress in date range
        List<ExerciseProgress> progressInRange = exerciseProgressRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get(FIELD_USER).get(FIELD_ID), userId),
                        cb.between(root.get(FIELD_LAST_ATTEMPT_AT), startDateTime, endDateTime)
                )
        );

        // Group progress by date
        Map<LocalDate, List<ExerciseProgress>> progressByDate = progressInRange.stream()
                .filter(ep -> ep.getLastAttemptAt() != null)
                .collect(Collectors.groupingBy(ep -> ep.getLastAttemptAt().toLocalDate()));

        // Create a response for each day in the range (including days with no activity)
        List<DailyActivityResponse> result = new ArrayList<>();

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<ExerciseProgress> dailyProgress = progressByDate.getOrDefault(current, List.of());

            int exercisesCompleted = (int) dailyProgress.stream()
                    .filter(ep -> CompletionStatus.COMPLETED.equals(ep.getStatus()))
                    .count();

            int timeSpentMinutes = dailyProgress.stream()
                    .filter(ep -> ep.getTimeSpent() != null)
                    .mapToInt(ExerciseProgress::getTimeSpent)
                    .sum() / 60; // Convert seconds to minutes

            double averageScore = dailyProgress.stream()
                    .filter(ep -> ep.getScore() != null)
                    .mapToDouble(ExerciseProgress::getScore)
                    .average()
                    .orElse(0.0);

            result.add(new DailyActivityResponse(
                    current,
                    exercisesCompleted,
                    timeSpentMinutes,
                    averageScore
            ));

            current = current.plusDays(1);
        }

        return result;
    }

    @Override
    @Cacheable(value = CacheNames.COURSE_PROGRESS, key = "'estimatedCompletion:' + #userId + ':' + #courseId")
    public LocalDate getEstimatedCourseCompletionDate(Long userId, Long courseId) {
        log.debug("Calculating estimated completion date for user {} and course {}", userId, courseId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND + courseId));

        UserCourseProgress progress = userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course progress not found for user " + userId + " and course " + courseId));

        // If course is complete, return completion date
        if (CompletionStatus.COMPLETED.equals(progress.getStatus()) && progress.getCompletedAt() != null) {
            return progress.getCompletedAt().toLocalDate();
        }

        // If progress is 0%, return a default date based on course difficulty
        if (progress.getCompletionPercentage() < 1.0) {
            return estimateDefaultCompletionDate(course);
        }

        // Get start date (or created at if no start date)
        LocalDate startDate = progress.getStartedAt() != null
                ? progress.getStartedAt().toLocalDate()
                : progress.getCreatedAt().toLocalDate();

        // Calculate days since started
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        if (daysSinceStart < 1) daysSinceStart = 1; // Avoid division by zero

        // Calculate progress per day
        double progressPerDay = progress.getCompletionPercentage() / daysSinceStart;

        // Apply adjustment based on recent activity
        double adjustedProgressPerDay = adjustProgressRate(userId, courseId, progressPerDay);

        // Calculate remaining days
        double remainingProgress = 100.0 - progress.getCompletionPercentage();
        long daysRemaining = (long) Math.ceil(remainingProgress / adjustedProgressPerDay);

        // Apply bounds to the estimate
        int minDays = 7;
        int maxDays = 365;
        daysRemaining = Math.max(minDays, Math.min(maxDays, daysRemaining));

        return LocalDate.now().plusDays(daysRemaining);
    }

    @Override
    @Cacheable(value = CacheNames.USER_PROGRESS, key = "'schedule:' + #userId")
    public LearningScheduleResponse generateOptimalLearningSchedule(Long userId) {
        log.debug("Generating optimal learning schedule for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        // Get user's learning patterns
        List<ExerciseProgress> exerciseProgresses = exerciseProgressRepository.findByUserId(userId);

        // Analyze best times for learning based on past activity
        Map<DayOfWeek, List<LocalDateTime>> activityByDayOfWeek = exerciseProgresses.stream()
                .filter(ep -> ep.getLastAttemptAt() != null)
                .collect(Collectors.groupingBy(
                        ep -> ep.getLastAttemptAt().getDayOfWeek(),
                        Collectors.mapping(ExerciseProgress::getLastAttemptAt, Collectors.toList())
                ));

        // Determine most active days
        List<DayOfWeek> mostActiveDays = activityByDayOfWeek.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .map(Map.Entry::getKey)
                .limit(3)
                .collect(Collectors.toList());

        // If no data yet, recommend balanced schedule
        if (mostActiveDays.isEmpty()) {
            mostActiveDays = List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY);
        }

        // Calculate optimal daily minutes based on past performance
        int totalTimeSpent = exerciseProgresses.stream()
                .filter(ep -> ep.getTimeSpent() != null)
                .mapToInt(ExerciseProgress::getTimeSpent)
                .sum() / 60; // Convert to minutes

        // Create learning schedule
        List<ScheduleSlot> recommendedSlots = new ArrayList<>();

        // Generate timeslots for each active day
        for (DayOfWeek day : mostActiveDays) {
            // Add morning slot
            recommendedSlots.add(new ScheduleSlot(
                    day.toString(),
                    "08:00",
                    "08:30",
                    "Vocabulary Practice"
            ));

            // Add evening slot
            recommendedSlots.add(new ScheduleSlot(
                    day.toString(),
                    "19:00",
                    "19:30",
                    "Grammar & Reading"
            ));
        }

        // Add weekend slot for listening practice
        recommendedSlots.add(new ScheduleSlot(
                DayOfWeek.SATURDAY.toString(),
                "10:00",
                "11:00",
                "Listening & Speaking"
        ));

        // Calculate optimal parameters
        int optimalDailyMinutes = Math.max(30, Math.min(120, totalTimeSpent / 30));
        int optimalSessionsPerWeek = Math.max(3, Math.min(7, mostActiveDays.size() * 2));
        boolean weekendsRecommended = true;

        return new LearningScheduleResponse(
                recommendedSlots,
                optimalDailyMinutes,
                optimalSessionsPerWeek,
                weekendsRecommended
        );
    }

    // Helper methods

    private LocalDate estimateDefaultCompletionDate(Course course) {
        // Base estimate based on course level
        int baseDays = switch (course.getLevel()) {
            case A1, A2 -> 120; // Beginner: ~4 months
            case B1, B2 -> 180; // Intermediate: ~6 months
            case C1, C2 -> 240; // Advanced: ~8 months
        };

        return LocalDate.now().plusDays(baseDays);
    }

    private double adjustProgressRate(Long userId, Long courseId, double baseProgressPerDay) {
        // Minimum progress rate to ensure reasonable estimates
        double minimumProgressRate = 0.5; // 0.5% per day = 200 days to complete

        if (baseProgressPerDay < minimumProgressRate) {
            return minimumProgressRate;
        }

        // Check recent activity (last 7 days)
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        long recentActivities = exerciseProgressRepository.findAll(
                (root, query, cb) -> cb.and(
                        cb.equal(root.get(FIELD_USER).get(FIELD_ID), userId),
                        cb.greaterThan(root.get(FIELD_LAST_ATTEMPT_AT), oneWeekAgo)
                )
        ).size();

        // Adjust based on recent activity level
        if (recentActivities > 20) {
            // Very active, progress faster
            return baseProgressPerDay * 1.3;
        } else if (recentActivities > 10) {
            // Active
            return baseProgressPerDay * 1.1;
        } else if (recentActivities > 5) {
            // Moderate activity
            return baseProgressPerDay;
        } else if (recentActivities > 0) {
            // Low activity
            return baseProgressPerDay * 0.8;
        } else {
            // No recent activity
            return baseProgressPerDay * 0.6;
        }
    }

    private CourseProgressMetrics mapToCourseMetrics(UserCourseProgress progress) {
        Course course = progress.getCourse();

        // Calculate estimated completion date based on current pace
        LocalDate startDate = progress.getStartedAt() != null ?
                progress.getStartedAt().toLocalDate() :
                progress.getCreatedAt().toLocalDate();

        LocalDate estimatedCompletionDate = calculateEstimatedCompletionDate(progress);

        return new CourseProgressMetrics(
                course.getId(),
                course.getTitle(),
                progress.getCompletionPercentage(),
                progress.getCompletedModules(),
                progress.getTotalModules(),
                progress.getCompletedLessons(),
                progress.getTotalLessons(),
                progress.getCompletedExercises(),
                progress.getTotalExercises(),
                progress.getAverageScore(),
                progress.getTotalTimeSpent(),
                startDate,
                estimatedCompletionDate
        );
    }

    private LocalDate calculateEstimatedCompletionDate(UserCourseProgress progress) {
        // If course is complete, return completion date
        if (CompletionStatus.COMPLETED.equals(progress.getStatus()) && progress.getCompletedAt() != null) {
            return progress.getCompletedAt().toLocalDate();
        }

        // Get start date
        LocalDate startDate = progress.getStartedAt() != null ?
                progress.getStartedAt().toLocalDate() :
                progress.getCreatedAt().toLocalDate();

        // If course just started, return a date 90 days from start
        if (progress.getCompletionPercentage() < 5.0) {
            return startDate.plusDays(90);
        }

        // Calculate days since start
        long daysSinceStart = ChronoUnit.DAYS.between(startDate, LocalDate.now());
        if (daysSinceStart < 1) daysSinceStart = 1; // Avoid division by zero

        // Calculate progress per day
        double progressPerDay = progress.getCompletionPercentage() / daysSinceStart;
        if (progressPerDay < 0.1) progressPerDay = 0.1; // Minimum progress rate

        // Calculate remaining days
        double remainingProgress = 100.0 - progress.getCompletionPercentage();
        long daysRemaining = (long) Math.ceil(remainingProgress / progressPerDay);

        // Return estimated completion date
        return LocalDate.now().plusDays(daysRemaining);
    }

    private Map<String, Double> generateSkillProficiencyMap(List<ExerciseProgress> exerciseProgresses) {
        // This would typically calculate proficiency for different language skills
        // For now, returning a simplified implementation with sample data
        Map<String, Double> proficiencyMap = new HashMap<>();
        proficiencyMap.put("grammar", 75.0);
        proficiencyMap.put("vocabulary", 82.0);
        proficiencyMap.put("listening", 68.0);
        proficiencyMap.put("reading", 79.0);
        proficiencyMap.put("writing", 72.0);
        proficiencyMap.put("speaking", 65.0);

        return proficiencyMap;
    }

    private WeeklyActivitySummary createWeeklyActivitySummary(List<ExerciseProgress> exerciseProgresses) {
        // Initialize maps for day-based data
        Map<String, Integer> exercisesByDay = new HashMap<>();
        Map<String, Integer> timeSpentByDay = new HashMap<>();

        // Initialize all days of week
        for (DayOfWeek day : DayOfWeek.values()) {
            exercisesByDay.put(day.name(), 0);
            timeSpentByDay.put(day.name(), 0);
        }

        // Count exercises and time spent by day of week
        for (ExerciseProgress progress : exerciseProgresses) {
            if (progress.getLastAttemptAt() == null) continue;

            String dayOfWeek = progress.getLastAttemptAt().getDayOfWeek().name();

            // Update exercise count
            exercisesByDay.put(dayOfWeek, exercisesByDay.get(dayOfWeek) + 1);

            // Update time spent
            if (progress.getTimeSpent() != null) {
                int minutesSpent = progress.getTimeSpent() / 60; // Convert seconds to minutes
                timeSpentByDay.put(dayOfWeek, timeSpentByDay.get(dayOfWeek) + minutesSpent);
            }
        }

        // Find most and least active days
        String mostActiveDay = findMaxValueKey(exercisesByDay);
        String leastActiveDay = findMinValueKey(exercisesByDay);

        // Calculate averages
        int totalExercises = exercisesByDay.values().stream().mapToInt(Integer::intValue).sum();
        int totalMinutes = timeSpentByDay.values().stream().mapToInt(Integer::intValue).sum();

        int averageDailyExercises = totalExercises / DAYS_IN_WEEK;
        int averageDailyMinutes = totalMinutes / DAYS_IN_WEEK;

        return new WeeklyActivitySummary(
                exercisesByDay,
                timeSpentByDay,
                mostActiveDay,
                leastActiveDay,
                averageDailyExercises,
                averageDailyMinutes
        );
    }

    private LearningPaceMetrics calculateLearningPaceMetrics(List<ExerciseProgress> exerciseProgresses) {
        // Filter valid entries with timestamps
        List<ExerciseProgress> validProgress = exerciseProgresses.stream()
                .filter(ep -> ep.getLastAttemptAt() != null)
                .collect(Collectors.toList());

        if (validProgress.isEmpty()) {
            return new LearningPaceMetrics(0, 0, 0, 0, "BEGINNER", false, false);
        }

        // Find earliest and latest activity
        LocalDateTime earliestActivity = validProgress.stream()
                .map(ExerciseProgress::getLastAttemptAt)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime latestActivity = validProgress.stream()
                .map(ExerciseProgress::getLastAttemptAt)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        // Calculate total days between first and last activity
        long totalDays = ChronoUnit.DAYS.between(earliestActivity, latestActivity) + 1;
        if (totalDays < 1) totalDays = 1; // Avoid division by zero

        // Calculate exercises per time period
        double exercisesPerDay = (double) validProgress.size() / totalDays;
        double exercisesPerWeek = exercisesPerDay * 7;

        // Calculate minutes per time period
        int totalSeconds = validProgress.stream()
                .filter(ep -> ep.getTimeSpent() != null)
                .mapToInt(ExerciseProgress::getTimeSpent)
                .sum();
        double totalMinutes = totalSeconds / 60.0;

        double minutesPerDay = totalMinutes / totalDays;
        double minutesPerWeek = minutesPerDay * 7;

        // Determine learning pace category
        String learningPaceCategory;
        if (exercisesPerWeek < 5) {
            learningPaceCategory = "CASUAL";
        } else if (exercisesPerWeek < 15) {
            learningPaceCategory = "STEADY";
        } else if (exercisesPerWeek < 30) {
            learningPaceCategory = "DEDICATED";
        } else {
            learningPaceCategory = "INTENSIVE";
        }

        // Check consistency (at least 3 days per week)
        boolean consistentLearning = exercisesPerWeek >= 3;

        // Check if on track for goals (simplified)
        boolean onTrackForGoals = exercisesPerWeek >= 10;

        return new LearningPaceMetrics(
                exercisesPerDay,
                exercisesPerWeek,
                minutesPerDay,
                minutesPerWeek,
                learningPaceCategory,
                consistentLearning,
                onTrackForGoals
        );
    }

    private String findMaxValueKey(Map<String, Integer> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private String findMinValueKey(Map<String, Integer> map) {
        return map.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
    }

    private double calculateStrengthScore(int exercisesCompleted, double averageScore) {
        // Simple formula that considers both quantity and quality
        if (exercisesCompleted == 0) return 0;

        // Base score is the average score (0-100)
        double baseScore = averageScore;

        // Apply a multiplier based on number of exercises completed
        // This gives more weight to skills with more practice
        double exerciseMultiplier = Math.min(1.0, exercisesCompleted / 10.0);

        return baseScore * (0.7 + 0.3 * exerciseMultiplier);
    }

    private String determineStrengthLevel(double strengthScore) {
        if (strengthScore >= 90) return "EXPERT";
        if (strengthScore >= 75) return "ADVANCED";
        if (strengthScore >= 60) return "INTERMEDIATE";
        if (strengthScore >= 40) return "BASIC";
        return "BEGINNER";
    }

    private String toTitleCase(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}