package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.entity.Exercise;
import ai.lingualeap.lingualeap.dao.entity.ExerciseProgress;
import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.LessonProgress;
import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.dao.entity.UserCourseProgress;
import ai.lingualeap.lingualeap.dao.repository.CourseRepository;
import ai.lingualeap.lingualeap.dao.repository.ExerciseProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.ExerciseRepository;
import ai.lingualeap.lingualeap.dao.repository.LessonProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.LessonRepository;
import ai.lingualeap.lingualeap.dao.repository.ModuleRepository;
import ai.lingualeap.lingualeap.dao.repository.UserCourseProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.request.ExerciseProgressUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseProgressResponse;
import ai.lingualeap.lingualeap.model.response.LessonProgressResponse;
import ai.lingualeap.lingualeap.model.response.UserCourseProgressResponse;
import ai.lingualeap.lingualeap.service.ProgressService;
import ai.lingualeap.lingualeap.service.mapper.ExerciseProgressMapper;
import ai.lingualeap.lingualeap.service.mapper.LessonProgressMapper;
import ai.lingualeap.lingualeap.service.mapper.UserCourseProgressMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressServiceImpl implements ProgressService {

    // Error message constants to avoid Multiple String Literals
    private static final String USER_NOT_FOUND = "User not found with id: ";
    private static final String EXERCISE_NOT_FOUND = "Exercise not found with id: ";
    private static final String LESSON_NOT_FOUND = "Lesson not found with id: ";
    private static final String COURSE_NOT_FOUND = "Course not found with id: ";
    private static final String COURSE_PROGRESS_NOT_FOUND = "Course progress not found for user ";
    private static final String AND_COURSE = " and course ";
    private static final String AND_EXERCISE = " and exercise ";
    private static final String FIELD_MODULE = "module";
    private static final String FIELD_COURSE = "course";
    private static final String FIELD_ID = "id";
    private static final String FIELD_LAST_ATTEMPT_AT = "lastAttemptAt";

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final ModuleRepository moduleRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseProgressRepository exerciseProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final UserCourseProgressRepository userCourseProgressRepository;
    private final ExerciseProgressMapper exerciseProgressMapper;
    private final LessonProgressMapper lessonProgressMapper;
    private final UserCourseProgressMapper userCourseProgressMapper;

    @Override
    @Transactional
    public ExerciseProgressResponse updateExerciseProgress(ExerciseProgressUpdateRequest request) {
        log.debug("Updating exercise progress for user {} and exercise {}", request.userId(), request.exerciseId());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + request.userId()));

        Exercise exercise = exerciseRepository.findById(request.exerciseId())
                .orElseThrow(() -> new EntityNotFoundException(EXERCISE_NOT_FOUND + request.exerciseId()));

        Optional<ExerciseProgress> existingProgressOpt = exerciseProgressRepository
                .findByUserIdAndExerciseIdAndIsDeleted(request.userId(), request.exerciseId(), false);

        ExerciseProgress exerciseProgress;

        if (existingProgressOpt.isPresent()) {
            exerciseProgress = existingProgressOpt.get();
            exerciseProgressMapper.updateEntityFromRequest(request, exerciseProgress);

            exerciseProgress.incrementAttemptCount();
            exerciseProgress.updateLastAttempt();

            if (CompletionStatus.COMPLETED.equals(request.status())) {
                exerciseProgress.markComplete();

                updateLessonProgress(user.getId(), exercise.getLesson().getId(), request.score(), request.timeSpent());

                Long courseId = exercise.getLesson().getModule().getCourse().getId();
                updateUserCourseProgress(user.getId(), courseId);
            } else if (CompletionStatus.IN_PROGRESS.equals(request.status())) {
                exerciseProgress.markInProgress();
            } else if (CompletionStatus.FAILED.equals(request.status())) {
                exerciseProgress.markFailed();
            }
        } else {
            // new record
            exerciseProgress = exerciseProgressMapper.toEntity(request, user, exercise);
            exerciseProgress.incrementAttemptCount();
            exerciseProgress.updateLastAttempt();

            if (CompletionStatus.COMPLETED.equals(request.status())) {
                exerciseProgress.markComplete();

                updateLessonProgress(user.getId(), exercise.getLesson().getId(), request.score(), request.timeSpent());

                Long courseId = exercise.getLesson().getModule().getCourse().getId();
                updateUserCourseProgress(user.getId(), courseId);
            }
        }

        exerciseProgress = exerciseProgressRepository.save(exerciseProgress);
        log.info("Exercise progress updated successfully for user {} and exercise {}", request.userId(), request.exerciseId());

        return exerciseProgressMapper.toResponse(exerciseProgress);
    }

    @Override
    public List<ExerciseProgressResponse> getExerciseProgressByUserId(Long userId) {
        log.debug("Getting exercise progress for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        List<ExerciseProgress> progressList = exerciseProgressRepository.findByUserId(userId);
        return exerciseProgressMapper.toResponseList(progressList);
    }

    @Override
    public ExerciseProgressResponse getExerciseProgressByUserIdAndExerciseId(Long userId, Long exerciseId) {
        log.debug("Getting exercise progress for user {} and exercise {}", userId, exerciseId);

        return exerciseProgressRepository.findByUserIdAndExerciseIdAndIsDeleted(userId, exerciseId, false)
                .map(exerciseProgressMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Exercise progress not found for user " + userId + AND_EXERCISE + exerciseId));
    }

    @Override
    public LessonProgressResponse getLessonProgressById(Long lessonProgressId) {
        log.debug("Getting lesson progress by id {}", lessonProgressId);

        return lessonProgressRepository.findById(lessonProgressId)
                .map(lessonProgressMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Lesson progress not found with id: " + lessonProgressId));
    }

    @Override
    public List<ExerciseProgressResponse> getExerciseProgressByUserIdAndLessonId(Long userId, Long lessonId) {
        log.debug("Getting exercise progress for user {} and lesson {}", userId, lessonId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        if (!lessonRepository.existsById(lessonId)) {
            throw new EntityNotFoundException(LESSON_NOT_FOUND + lessonId);
        }

        List<ExerciseProgress> progressList = exerciseProgressRepository.findByLessonIdAndUserId(lessonId, userId);
        return exerciseProgressMapper.toResponseList(progressList);
    }

    @Override
    public UserCourseProgressResponse getUserCourseProgressByUserIdAndCourseId(Long userId, Long courseId) {
        log.debug("Getting course progress for user {} and course {}", userId, courseId);

        return userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .map(userCourseProgressMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException(
                        COURSE_PROGRESS_NOT_FOUND + userId + AND_COURSE + courseId));
    }

    @Override
    public List<UserCourseProgressResponse> getUserCourseProgressByUserId(Long userId) {
        log.debug("Getting course progress for user {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        List<UserCourseProgress> progressList = userCourseProgressRepository.findByUserId(userId);
        return userCourseProgressMapper.toResponseList(progressList);
    }

    @Override
    public List<UserCourseProgressResponse> getUserCourseProgressByUserIdAndStatus(Long userId, CompletionStatus status) {
        log.debug("Getting course progress for user {} with status {}", userId, status);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        List<UserCourseProgress> progressList = userCourseProgressRepository.findByUserIdAndStatus(userId, status);
        return userCourseProgressMapper.toResponseList(progressList);
    }

    @Override
    @Transactional
    public UserCourseProgressResponse enrollUserToCourse(Long userId, Long courseId) {
        log.debug("Enrolling user {} to course {}", userId, courseId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + userId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException(COURSE_NOT_FOUND + courseId));

        Optional<UserCourseProgress> existingEnrollment = userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId);

        if (existingEnrollment.isPresent()) {
            log.info("User {} is already enrolled to course {}", userId, courseId);
            return userCourseProgressMapper.toResponse(existingEnrollment.get());
        }

        int totalModules = moduleRepository.findByCourseIdOrderBySequenceAsc(courseId).size();

        int totalLessons = 0;
        int totalExercises = 0;

        List<Lesson> allLessons = lessonRepository.findAll(
                (root, query, cb) -> cb.equal(root.get(FIELD_MODULE).get(FIELD_COURSE).get(FIELD_ID), courseId)
        );

        totalLessons = allLessons.size();

        for (Lesson lesson : allLessons) {
            totalExercises += exerciseRepository.findByLessonIdOrderBySequenceAsc(lesson.getId()).size();
        }

        UserCourseProgress progress = userCourseProgressMapper.toEntity(user, course, totalModules, totalLessons, totalExercises);
        progress.start();

        progress = userCourseProgressRepository.save(progress);
        log.info("User {} enrolled to course {} successfully", userId, courseId);

        return userCourseProgressMapper.toResponse(progress);
    }

    @Override
    public List<ExerciseProgressResponse> getUserActivitySummary(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting activity summary for user {} between {} and {}", userId, startDate, endDate);

        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(USER_NOT_FOUND + userId);
        }

        List<ExerciseProgress> activities = exerciseProgressRepository.findAll(
                (root, query, cb) -> {
                    query.orderBy(cb.desc(root.get(FIELD_LAST_ATTEMPT_AT)));
                    return cb.and(
                            cb.equal(root.get("user").get(FIELD_ID), userId),
                            cb.between(root.get(FIELD_LAST_ATTEMPT_AT), startDate, endDate)
                    );
                }
        );

        return exerciseProgressMapper.toResponseList(activities);
    }

    @Override
    @Transactional
    public UserCourseProgressResponse calculateAndUpdateUserCourseProgress(Long userId, Long courseId) {
        log.debug("Calculating and updating course progress for user {} and course {}", userId, courseId);

        UserCourseProgress progress = userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        COURSE_PROGRESS_NOT_FOUND + userId + AND_COURSE + courseId));

        updateCourseStatistics(progress);

        progress = userCourseProgressRepository.save(progress);
        log.info("Course progress updated for user {} and course {}", userId, courseId);

        return userCourseProgressMapper.toResponse(progress);
    }

    @Transactional
    protected void updateLessonProgress(Long userId, Long lessonId, Double score, Integer timeSpent) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new EntityNotFoundException(LESSON_NOT_FOUND + lessonId));

        // check lesson progress
        LessonProgress lessonProgress = lessonProgressRepository.findByLessonId(lessonId).stream()
                .findFirst()
                .orElseGet(() -> {
                    LessonProgress newProgress = lessonProgressMapper.toEntity(lesson);
                    return lessonProgressRepository.save(newProgress);
                });

        lessonProgress.incrementTotalAttempts();

        if (score != null) {
            if (score >= lesson.getMinRequiredScore()) {
                lessonProgress.incrementSuccessfulAttempts();
            }
            lessonProgress.updateAverageScore(score);
        }

        if (timeSpent != null) {
            lessonProgress.addTimeSpent(timeSpent);
        }

        Long totalExercises = exerciseRepository.count(
                (root, query, cb) -> cb.equal(root.get("lesson").get(FIELD_ID), lessonId)
        );

        Long completedExercises = exerciseProgressRepository.countCompletedExercisesByLessonIdAndUserId(lessonId, userId);

        if (totalExercises > 0 && totalExercises.equals(completedExercises)) {
            lessonProgress.markComplete();
        }

        lessonProgressRepository.save(lessonProgress);
    }

    @Transactional
    protected void updateUserCourseProgress(Long userId, Long courseId) {
        UserCourseProgress progress = userCourseProgressRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new EntityNotFoundException(
                        COURSE_PROGRESS_NOT_FOUND + userId + AND_COURSE + courseId));

        progress.updateLastActivity();

        updateCourseStatistics(progress);

        userCourseProgressRepository.save(progress);
    }

    private void updateCourseStatistics(UserCourseProgress progress) {
        final Long courseId = progress.getCourse().getId();
        final Long userId = progress.getUser().getId();

        // calculate completion modules
        List<Lesson> allLessons = lessonRepository.findAll(
                (root, query, cb) -> cb.equal(root.get(FIELD_MODULE).get(FIELD_COURSE).get(FIELD_ID), courseId)
        );

        int completedLessons = 0;
        int completedExercises = 0;
        double totalScore = 0;
        int scoreCount = 0;

        for (Lesson lesson : allLessons) {
            // check lesson completion
            List<ExerciseProgress> lessonExerciseProgress = exerciseProgressRepository.findByLessonIdAndUserId(lesson.getId(), userId);

            if (!lessonExerciseProgress.isEmpty()) {
                boolean allExercisesCompleted = lessonExerciseProgress.stream()
                        .allMatch(ep -> CompletionStatus.COMPLETED.equals(ep.getStatus()));

                if (allExercisesCompleted) {
                    completedLessons++;
                }

                long completedExercisesInLesson = lessonExerciseProgress.stream()
                        .filter(ep -> CompletionStatus.COMPLETED.equals(ep.getStatus()))
                        .count();

                completedExercises += completedExercisesInLesson;

                final double[] tempTotalScore = {totalScore};
                final int[] tempScoreCount = {scoreCount};

                // collect data for average score
                lessonExerciseProgress.stream()
                        .filter(ep -> ep.getScore() != null)
                        .forEach(ep -> {
                            tempTotalScore[0] += ep.getScore();
                            tempScoreCount[0]++;
                        });

                totalScore = tempTotalScore[0];
                scoreCount = tempScoreCount[0];
            }
        }

        double averageScore = scoreCount > 0 ? totalScore / scoreCount : 0;

        int completedModules = 0;
        var modules = moduleRepository.findByCourseIdOrderBySequenceAsc(courseId);

        for (var module : modules) {
            var moduleLessons = lessonRepository.findByModuleIdOrderBySequenceAsc(module.getId());

            if (!moduleLessons.isEmpty()) {
                boolean allLessonsCompleted = true;

                for (Lesson lesson : moduleLessons) {
                    List<ExerciseProgress> lessonExerciseProgress = exerciseProgressRepository.findByLessonIdAndUserId(lesson.getId(), userId);

                    if (lessonExerciseProgress.isEmpty()) {
                        allLessonsCompleted = false;
                        break;
                    }

                    boolean lessonCompleted = lessonExerciseProgress.stream()
                            .allMatch(ep -> CompletionStatus.COMPLETED.equals(ep.getStatus()));

                    if (!lessonCompleted) {
                        allLessonsCompleted = false;
                        break;
                    }
                }

                if (allLessonsCompleted) {
                    completedModules++;
                }
            }
        }

        // update statistics
        progress.setCompletedLessons(completedLessons);
        progress.setCompletedExercises(completedExercises);
        progress.setCompletedModules(completedModules);
        progress.setAverageScore(averageScore);

        // show completion percentage
        if (progress.getTotalLessons() > 0) {
            double completionPercentage = (double) completedLessons / progress.getTotalLessons() * 100;
            progress.setCompletionPercentage(completionPercentage);
        }

        if (completedLessons == 0) {
            progress.setStatus(CompletionStatus.NOT_STARTED);
        } else if (completedLessons == progress.getTotalLessons()) {
            progress.setStatus(CompletionStatus.COMPLETED);
            progress.setCompletedAt(LocalDateTime.now());
        } else {
            progress.setStatus(CompletionStatus.IN_PROGRESS);
        }
    }
}
