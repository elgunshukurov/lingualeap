package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.Module;
import ai.lingualeap.lingualeap.dao.repository.LessonRepository;
import ai.lingualeap.lingualeap.dao.repository.ModuleRepository;
import ai.lingualeap.lingualeap.dao.repository.UserProgressRepository;
import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.request.LessonCreateRequest;
import ai.lingualeap.lingualeap.model.request.LessonUpdateRequest;
import ai.lingualeap.lingualeap.model.response.LessonResponse;
import ai.lingualeap.lingualeap.service.impl.LessonServiceImpl;
import ai.lingualeap.lingualeap.service.mapper.LessonMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {
    private static final String TEST_LESSON_TITLE = "Test Lesson";
    private static final String TEST_DESCRIPTION = "Description";
    private static final String THEORY_CONTENT = "Theory content";

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    @Mock
    private LessonMapper lessonMapper;

    @InjectMocks
    private LessonServiceImpl lessonService;

    private Module testModule;
    private Lesson testLesson;
    private LessonCreateRequest createRequest;
    private LessonUpdateRequest updateRequest;
    private LessonResponse lessonResponse;

    @BeforeEach
    void setUp() {
        testModule = new Module();
        testModule.setId(1L);
        testModule.setTitle("Test Module");

        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitle(TEST_LESSON_TITLE);
        testLesson.setType(LessonType.THEORY);
        testLesson.setLevel(LessonLevel.BEGINNER);
        testLesson.setStatus(LessonStatus.DRAFT);
        testLesson.setModule(testModule);

        createRequest = new LessonCreateRequest(
                TEST_LESSON_TITLE,
                TEST_DESCRIPTION,
                LessonType.THEORY,
                LessonLevel.BEGINNER,
                1L,
                1,
                70,
                30,
                THEORY_CONTENT,
                false,
                null,
                Set.of(),
                Set.of()
        );

        updateRequest = new LessonUpdateRequest(
                "Updated Lesson",
                "Updated Description",
                LessonType.PRACTICE,
                LessonLevel.INTERMEDIATE,
                LessonStatus.PUBLISHED,
                1,
                80,
                45,
                "Updated content",
                true,
                "AI prompt",
                Set.of(),
                Set.of()
        );

        lessonResponse = new LessonResponse(
                1L,
                TEST_LESSON_TITLE,
                TEST_DESCRIPTION,
                LessonType.THEORY,
                LessonLevel.BEGINNER,
                LessonStatus.DRAFT,
                1,
                70,
                30,
                THEORY_CONTENT,
                false,
                null,
                null,
                List.of(),
                Set.of(),
                Set.of(),
                null,
                null
        );
    }

    @Test
    void createLesson_Success() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(testModule));
        when(lessonMapper.toEntity(createRequest)).thenReturn(testLesson);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);
        when(lessonMapper.toResponse(testLesson)).thenReturn(lessonResponse);

        LessonResponse result = lessonService.createLesson(createRequest);

        assertNotNull(result);
        assertEquals(TEST_LESSON_TITLE, result.title());
        assertEquals(LessonType.THEORY, result.type());

        verify(moduleRepository).findById(1L);
        verify(lessonRepository).save(any(Lesson.class));
        verify(lessonMapper).toResponse(any(Lesson.class));
    }

    @Test
    void createLesson_ModuleNotFound() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                lessonService.createLesson(createRequest)
        );

        verify(lessonRepository, never()).save(any(Lesson.class));
    }

    @Test
    void updateLesson_Success() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(testLesson));
        when(lessonRepository.save(any(Lesson.class))).thenReturn(testLesson);
        when(lessonMapper.toResponse(testLesson)).thenReturn(lessonResponse);

        LessonResponse result = lessonService.updateLesson(1L, updateRequest);

        assertNotNull(result);
        verify(lessonRepository).save(any(Lesson.class));
        verify(lessonMapper).toResponse(any(Lesson.class));
    }

    @Test
    void searchLessons_Success() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Lesson> lessonPage = new PageImpl<>(List.of(testLesson));

        when(lessonRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(lessonPage);
        when(lessonMapper.toResponse(any(Lesson.class))).thenReturn(lessonResponse);

        Page<LessonResponse> result = lessonService.searchLessons(
                LessonType.THEORY,
                LessonLevel.BEGINNER,
                LessonStatus.DRAFT,
                1L,
                pageRequest
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(lessonRepository).findAll(any(Specification.class), any(PageRequest.class));
    }
}
