package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.entity.Exercise;
import ai.lingualeap.lingualeap.dao.entity.ExerciseProgress;
import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.Module;
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
import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import ai.lingualeap.lingualeap.model.enums.ExerciseType;
import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.request.ExerciseProgressUpdateRequest;
import ai.lingualeap.lingualeap.model.response.ExerciseProgressResponse;
import ai.lingualeap.lingualeap.model.response.UserCourseProgressResponse;
import ai.lingualeap.lingualeap.service.impl.ProgressServiceImpl;
import ai.lingualeap.lingualeap.service.mapper.ExerciseProgressMapper;
import ai.lingualeap.lingualeap.service.mapper.LessonProgressMapper;
import ai.lingualeap.lingualeap.service.mapper.UserCourseProgressMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTest {

    public static final String TEST_ANSWER = "Test answer";
    public static final String GOOD_JOB = "Good job!";
    public static final String TESTUSER = "testuser";
    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ExerciseProgressRepository exerciseProgressRepository;

    @Mock
    private LessonProgressRepository lessonProgressRepository;

    @Mock
    private UserCourseProgressRepository userCourseProgressRepository;

    @Mock
    private ExerciseProgressMapper exerciseProgressMapper;

    @Mock
    private LessonProgressMapper lessonProgressMapper;

    @Mock
    private UserCourseProgressMapper userCourseProgressMapper;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private User testUser;
    private Course testCourse;
    private Module testModule;
    private Lesson testLesson;
    private Exercise testExercise;
    private ExerciseProgress testExerciseProgress;
    private UserCourseProgress testUserCourseProgress;
    private ExerciseProgressUpdateRequest updateRequest;
    private ExerciseProgressResponse exerciseProgressResponse;
    private UserCourseProgressResponse userCourseProgressResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(TESTUSER);
        testUser.setEmail("test@example.com");

        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");
        testCourse.setTargetLanguage("English");
        testCourse.setSourceLanguage("Turkish");
        testCourse.setLevel(CourseLevel.A1);

        testModule = new Module();
        testModule.setId(1L);
        testModule.setTitle("Test Module");
        testModule.setSequence(1);
        testModule.setCourse(testCourse);

        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitle("Test Lesson");
        testLesson.setType(LessonType.THEORY);
        testLesson.setLevel(LessonLevel.BEGINNER);
        testLesson.setSequence(1);
        testLesson.setMinRequiredScore(70);
        testLesson.setModule(testModule);

        testExercise = new Exercise();
        testExercise.setId(1L);
        testExercise.setTitle("Test Exercise");
        testExercise.setType(ExerciseType.MULTIPLE_CHOICE);
        testExercise.setPoints(10);
        testExercise.setSequence(1);
        testExercise.setLesson(testLesson);

        testExerciseProgress = new ExerciseProgress();
        testExerciseProgress.setId(1L);
        testExerciseProgress.setUser(testUser);
        testExerciseProgress.setExercise(testExercise);
        testExerciseProgress.setScore(85.0);
        testExerciseProgress.setAttemptCount(1);
        testExerciseProgress.setTimeSpent(300);
        testExerciseProgress.setStatus(CompletionStatus.COMPLETED);
        testExerciseProgress.setLastAttemptAt(LocalDateTime.now());

        testUserCourseProgress = new UserCourseProgress();
        testUserCourseProgress.setId(1L);
        testUserCourseProgress.setUser(testUser);
        testUserCourseProgress.setCourse(testCourse);
        testUserCourseProgress.setTotalModules(1);
        testUserCourseProgress.setTotalLessons(3);
        testUserCourseProgress.setTotalExercises(10);
        testUserCourseProgress.setCompletedModules(0);
        testUserCourseProgress.setCompletedLessons(1);
        testUserCourseProgress.setCompletedExercises(2);
        testUserCourseProgress.setStatus(CompletionStatus.IN_PROGRESS);

        updateRequest = new ExerciseProgressUpdateRequest(
                1L,
                1L,
                85.0,
                300,
                TEST_ANSWER,
                GOOD_JOB,
                CompletionStatus.COMPLETED
        );

        exerciseProgressResponse = new ExerciseProgressResponse(
                1L,
                1L,
                TESTUSER,
                null,
                85.0,
                1,
                300,
                TEST_ANSWER,
                GOOD_JOB,
                CompletionStatus.COMPLETED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        userCourseProgressResponse = new UserCourseProgressResponse(
                1L,
                null,
                null,
                0,
                1,
                1,
                3,
                2,
                10,
                85.0,
                33.3,
                600,
                CompletionStatus.IN_PROGRESS,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void updateExerciseProgress_UserNotFound_ThrowsException() {
        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Call method and verify
        assertThrows(EntityNotFoundException.class, () -> progressService.updateExerciseProgress(updateRequest));
    }

    @Test
    void updateExerciseProgress_ExerciseNotFound_ThrowsException() {
        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(exerciseRepository.findById(1L)).thenReturn(Optional.empty());

        // Call method and verify
        assertThrows(EntityNotFoundException.class, () -> progressService.updateExerciseProgress(updateRequest));
    }

    @Test
    void getExerciseProgressByUserId_Success() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(true);
        when(exerciseProgressRepository.findByUserId(1L)).thenReturn(List.of(testExerciseProgress));
        when(exerciseProgressMapper.toResponseList(List.of(testExerciseProgress)))
                .thenReturn(List.of(exerciseProgressResponse));

        // Call method
        List<ExerciseProgressResponse> result = progressService.getExerciseProgressByUserId(1L);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void getExerciseProgressByUserId_UserNotFound_ThrowsException() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(false);

        // Call method and verify
        assertThrows(EntityNotFoundException.class, () -> progressService.getExerciseProgressByUserId(1L));
    }

    @Test
    void getExerciseProgressByUserIdAndExerciseId_Success() {
        // Mock behavior
        when(exerciseProgressRepository.findByUserIdAndExerciseIdAndIsDeleted(1L, 1L, false))
                .thenReturn(Optional.of(testExerciseProgress));
        when(exerciseProgressMapper.toResponse(testExerciseProgress)).thenReturn(exerciseProgressResponse);

        // Call method
        ExerciseProgressResponse result = progressService.getExerciseProgressByUserIdAndExerciseId(1L, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(85.0, result.score());
    }

    @Test
    void getExerciseProgressByUserIdAndExerciseId_NotFound_ThrowsException() {
        // Mock behavior
        when(exerciseProgressRepository.findByUserIdAndExerciseIdAndIsDeleted(1L, 1L, false))
                .thenReturn(Optional.empty());

        // Call method and verify
        assertThrows(EntityNotFoundException.class, () ->
                progressService.getExerciseProgressByUserIdAndExerciseId(1L, 1L));
    }

    @Test
    void getUserCourseProgressByUserIdAndCourseId_Success() {
        // Mock behavior
        when(userCourseProgressRepository.findByUserIdAndCourseId(1L, 1L))
                .thenReturn(Optional.of(testUserCourseProgress));
        when(userCourseProgressMapper.toResponse(testUserCourseProgress)).thenReturn(userCourseProgressResponse);

        // Call method
        UserCourseProgressResponse result = progressService.getUserCourseProgressByUserIdAndCourseId(1L, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(CompletionStatus.IN_PROGRESS, result.status());
    }

    @Test
    void getUserCourseProgressByUserIdAndCourseId_NotFound_ThrowsException() {
        // Mock behavior
        when(userCourseProgressRepository.findByUserIdAndCourseId(1L, 1L))
                .thenReturn(Optional.empty());

        // Call method and verify
        assertThrows(EntityNotFoundException.class, () ->
                progressService.getUserCourseProgressByUserIdAndCourseId(1L, 1L));
    }

    @Test
    void getUserCourseProgressByUserId_Success() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userCourseProgressRepository.findByUserId(1L)).thenReturn(List.of(testUserCourseProgress));
        when(userCourseProgressMapper.toResponseList(List.of(testUserCourseProgress)))
                .thenReturn(List.of(userCourseProgressResponse));

        // Call method
        List<UserCourseProgressResponse> result = progressService.getUserCourseProgressByUserId(1L);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
    }

    @Test
    void getUserCourseProgressByUserId_UserNotFound_ThrowsException() {
        // Mock behavior
        when(userRepository.existsById(1L)).thenReturn(false);

        // Call method and verify
        assertThrows(EntityNotFoundException.class, () -> progressService.getUserCourseProgressByUserId(1L));
    }

    @Test
    void enrollUserToCourse_Success() {
        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userCourseProgressRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.empty());
        when(moduleRepository.findByCourseIdOrderBySequenceAsc(1L)).thenReturn(List.of(testModule));
        when(lessonRepository.findAll(any(Specification.class))).thenReturn(List.of(testLesson));
        when(exerciseRepository.findByLessonIdOrderBySequenceAsc(1L)).thenReturn(List.of(testExercise));
        when(userCourseProgressMapper.toEntity(any(User.class), any(Course.class), any(Integer.class),
                any(Integer.class), any(Integer.class))).thenReturn(testUserCourseProgress);
        when(userCourseProgressRepository.save(testUserCourseProgress)).thenReturn(testUserCourseProgress);
        when(userCourseProgressMapper.toResponse(testUserCourseProgress)).thenReturn(userCourseProgressResponse);

        // Call method
        UserCourseProgressResponse result = progressService.enrollUserToCourse(1L, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(userCourseProgressRepository).save(testUserCourseProgress);
    }

    @Test
    void enrollUserToCourse_AlreadyEnrolled_ReturnsExistingProgress() {
        // Mock behavior
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(userCourseProgressRepository.findByUserIdAndCourseId(1L, 1L))
                .thenReturn(Optional.of(testUserCourseProgress));
        when(userCourseProgressMapper.toResponse(testUserCourseProgress)).thenReturn(userCourseProgressResponse);

        // Call method
        UserCourseProgressResponse result = progressService.enrollUserToCourse(1L, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.id());
        // Verify that save is not called since user is already enrolled
        verify(userCourseProgressRepository, times(0)).save(any(UserCourseProgress.class));
    }

    @Test
    void calculateAndUpdateUserCourseProgress_Success() {
        // Mock behavior
        Map<String, Object> mockStatistics = new HashMap<>();
        mockStatistics.put("completedLessons", 1);
        mockStatistics.put("completedExercises", 2);
        mockStatistics.put("completedModules", 0);
        mockStatistics.put("averageScore", 85.0);
        when(userCourseProgressRepository.getCourseLessonStatistics(1L, 1L)).thenReturn(mockStatistics);

        when(userCourseProgressRepository.findByUserIdAndCourseId(1L, 1L))
                .thenReturn(Optional.of(testUserCourseProgress));
        when(userCourseProgressRepository.save(testUserCourseProgress)).thenReturn(testUserCourseProgress);
        when(userCourseProgressMapper.toResponse(testUserCourseProgress)).thenReturn(userCourseProgressResponse);

        // Call method
        UserCourseProgressResponse result = progressService.calculateAndUpdateUserCourseProgress(1L, 1L);

        // Verify
        assertNotNull(result);
        assertEquals(1L, result.id());
        verify(userCourseProgressRepository).save(testUserCourseProgress);
    }

    @Test
    void calculateAndUpdateUserCourseProgress_NotFound_ThrowsException() {
        // Mock behavior
        when(userCourseProgressRepository.findByUserIdAndCourseId(1L, 1L)).thenReturn(Optional.empty());

        // Call method and verify
        assertThrows(EntityNotFoundException.class, () ->
                progressService.calculateAndUpdateUserCourseProgress(1L, 1L));
    }
}
