package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.entity.ExerciseProgress;
import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.dao.entity.UserCourseProgress;
import ai.lingualeap.lingualeap.dao.repository.CourseRepository;
import ai.lingualeap.lingualeap.dao.repository.ExerciseProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.UserCourseProgressRepository;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.enums.CompletionStatus;
import ai.lingualeap.lingualeap.model.response.LearningRecommendationResponse;
import ai.lingualeap.lingualeap.model.response.ProgressMetricsResponse;
import ai.lingualeap.lingualeap.model.response.SkillStrengthResponse;
import ai.lingualeap.lingualeap.service.impl.ProgressAnalyticsServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressAnalyticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ExerciseProgressRepository exerciseProgressRepository;

    @Mock
    private UserCourseProgressRepository userCourseProgressRepository;

    @InjectMocks
    private ProgressAnalyticsServiceImpl progressAnalyticsService;

    private User testUser;
    private Course testCourse;
    private UserCourseProgress testUserCourseProgress;
    private List<ExerciseProgress> testExerciseProgresses;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");

        testUserCourseProgress = new UserCourseProgress();
        testUserCourseProgress.setId(1L);
        testUserCourseProgress.setUser(testUser);
        testUserCourseProgress.setCourse(testCourse);
        testUserCourseProgress.setCompletionPercentage(50.0);
        testUserCourseProgress.setStatus(CompletionStatus.IN_PROGRESS);
        testUserCourseProgress.setTotalModules(5);
        testUserCourseProgress.setCompletedModules(2);
        testUserCourseProgress.setTotalLessons(20);
        testUserCourseProgress.setCompletedLessons(10);
        testUserCourseProgress.setTotalExercises(100);
        testUserCourseProgress.setCompletedExercises(50);
        testUserCourseProgress.setAverageScore(85.0);

        testExerciseProgresses = new ArrayList<>();
        // Add sample exercise progress
        ExerciseProgress progress1 = new ExerciseProgress();
        progress1.setId(1L);
        progress1.setUser(testUser);
        progress1.setScore(80.0);
        progress1.setTimeSpent(300);
        progress1.setStatus(CompletionStatus.COMPLETED);
        progress1.setLastAttemptAt(LocalDateTime.now().minusDays(1));

        ExerciseProgress progress2 = new ExerciseProgress();
        progress2.setId(2L);
        progress2.setUser(testUser);
        progress2.setScore(90.0);
        progress2.setTimeSpent(240);
        progress2.setStatus(CompletionStatus.COMPLETED);
        progress2.setLastAttemptAt(LocalDateTime.now().minusDays(2));

        testExerciseProgresses.add(progress1);
        testExerciseProgresses.add(progress2);
    }

    @Test
    void getUserProgressMetrics_Success() {
        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userCourseProgressRepository.findByUserId(1L)).thenReturn(List.of(testUserCourseProgress));
        when(exerciseProgressRepository.findByUserId(1L)).thenReturn(testExerciseProgresses);
        when(exerciseProgressRepository.findAll(any(Specification.class))).thenReturn(testExerciseProgresses);

        // Call method
        ProgressMetricsResponse response = progressAnalyticsService.getUserProgressMetrics(1L);

        // Verify
        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("testuser", response.username());
        assertEquals(1, response.activeCourses());
        assertEquals(0, response.completedCourses());
        assertEquals(2, response.totalExercisesCompleted());
        assertEquals(50.0, response.overallCompletionPercentage());
        assertTrue(response.averageScore() > 0);
        assertTrue(response.totalTimeSpentMinutes() > 0);
    }

    @Test
    void getUserProgressMetrics_UserNotFound() {
        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Verify
        assertThrows(EntityNotFoundException.class, () -> progressAnalyticsService.getUserProgressMetrics(1L));
    }

    @Test
    void analyzeUserSkillStrengths_Success() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(true);
        when(exerciseProgressRepository.findByUserId(1L)).thenReturn(testExerciseProgresses);

        // Call method
        List<SkillStrengthResponse> skillStrengths = progressAnalyticsService.analyzeUserSkillStrengths(1L);

        // Verify
        assertNotNull(skillStrengths);
        // Note: The implementation returns empty list for now, but in future it'll have real data
    }

    @Test
    void analyzeUserSkillStrengths_UserNotFound() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(false);

        // Verify
        assertThrows(EntityNotFoundException.class, () -> progressAnalyticsService.analyzeUserSkillStrengths(1L));
    }

    @Test
    void generateLearningRecommendations_Success() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userCourseProgressRepository.findByUserId(1L)).thenReturn(List.of(testUserCourseProgress));
        when(exerciseProgressRepository.findByUserId(1L)).thenReturn(testExerciseProgresses);
        when(userRepository.existsById(1L)).thenReturn(true);

        // Call method
        List<LearningRecommendationResponse> recommendations = progressAnalyticsService.generateLearningRecommendations(1L);

        // Verify
        assertNotNull(recommendations);
        assertTrue(recommendations.size() > 0);
    }

    @Test
    void generateLearningRecommendations_UserNotFound() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(false);

        // Verify
        assertThrows(EntityNotFoundException.class, () -> progressAnalyticsService.generateLearningRecommendations(1L));
    }

    @Test
    void getUserStudyStreak_Success() {
        // Mock behavior
        when(exerciseProgressRepository.findAll(any(Specification.class))).thenReturn(testExerciseProgresses);

        // Call method
        int streak = progressAnalyticsService.getUserStudyStreak(1L);

        // Verify
        assertTrue(streak >= 0);
    }

    @Test
    void getUserStudyStreak_NoActivity() {
        // Mock behavior
        when(exerciseProgressRepository.findAll(any(Specification.class))).thenReturn(new ArrayList<>());

        // Call method
        int streak = progressAnalyticsService.getUserStudyStreak(1L);

        // Verify
        assertEquals(0, streak);
    }

    @Test
    void getDailyActivitySummary_Success() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(true);
        when(exerciseProgressRepository.findAll(any(Specification.class))).thenReturn(testExerciseProgresses);

        // Call method
        List<ProgressAnalyticsService.DailyActivityResponse> activities =
                progressAnalyticsService.getDailyActivitySummary(1L, LocalDate.now().minusDays(7), LocalDate.now());

        // Verify
        assertNotNull(activities);
        assertEquals(8, activities.size()); // 8 days including start and end date
    }

    @Test
    void getEstimatedCourseCompletionDate_Success() {
        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userCourseProgressRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.of(testUserCourseProgress));
        when(exerciseProgressRepository.findAll(any(Specification.class))).thenReturn(testExerciseProgresses);

        // Call method
        LocalDate completionDate = progressAnalyticsService.getEstimatedCourseCompletionDate(1L, 1L);

        // Verify
        assertNotNull(completionDate);
        assertTrue(completionDate.isAfter(LocalDate.now()));
    }

    @Test
    void generateOptimalLearningSchedule_Success() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(true);
        when(exerciseProgressRepository.findByUserId(1L)).thenReturn(testExerciseProgresses);

        // Call method
        ProgressAnalyticsService.LearningScheduleResponse schedule = progressAnalyticsService.generateOptimalLearningSchedule(1L);

        // Verify
        assertNotNull(schedule);
        assertNotNull(schedule.recommendedSlots());
        assertTrue(schedule.recommendedSlots().size() > 0);
        assertTrue(schedule.optimalDailyMinutes() > 0);
        assertTrue(schedule.optimalSessionsPerWeek() > 0);
    }
}
