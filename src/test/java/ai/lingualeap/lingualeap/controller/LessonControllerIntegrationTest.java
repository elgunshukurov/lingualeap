package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.config.BaseIntegrationTest;
import ai.lingualeap.lingualeap.dao.entity.Course;
import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.Module;
import ai.lingualeap.lingualeap.dao.repository.CourseRepository;
import ai.lingualeap.lingualeap.dao.repository.LessonRepository;
import ai.lingualeap.lingualeap.dao.repository.ModuleRepository;
import ai.lingualeap.lingualeap.model.enums.CourseLevel;
import ai.lingualeap.lingualeap.model.enums.LessonLevel;
import ai.lingualeap.lingualeap.model.enums.LessonStatus;
import ai.lingualeap.lingualeap.model.enums.LessonType;
import ai.lingualeap.lingualeap.model.request.LessonCreateRequest;
import ai.lingualeap.lingualeap.model.response.LessonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LessonControllerIntegrationTest extends BaseIntegrationTest {

    private static final String API_LESSONS = "/api/v1/lessons";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_LESSON_TITLE = "Test Lesson";
    private static final String INTEGRATION_TEST_LESSON = "Integration Test Lesson";
    private static final String JSON_TITLE_PATH = "$.title";
    private static final String JSON_TYPE_PATH = "$.type";
    private static final String THEORY_TYPE = "THEORY";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    private Module testModule;
    private LessonCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        lessonRepository.deleteAll();
        moduleRepository.deleteAll();
        courseRepository.deleteAll();

        Course testCourse = new Course();
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Course Description");
        testCourse.setTargetLanguage("English");
        testCourse.setSourceLanguage("Spanish");
        testCourse.setLevel(CourseLevel.A1);
        testCourse = courseRepository.save(testCourse);


        testModule = new Module();
        testModule.setTitle("Test Module");
        testModule.setDescription(TEST_DESCRIPTION);
        testModule.setSequence(1);
        testModule.setCourse(testCourse);
        testModule = moduleRepository.save(testModule);

        createRequest = new LessonCreateRequest(
                INTEGRATION_TEST_LESSON,
                TEST_DESCRIPTION,
                LessonType.THEORY,
                LessonLevel.BEGINNER,
                testModule.getId(),
                1,
                70,
                30,
                "Theory content",
                false,
                null,
                Set.of(),
                Set.of()
        );
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void createLesson_Success() throws Exception {
        MvcResult result = mockMvc.perform(post(API_LESSONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(JSON_TITLE_PATH).value(INTEGRATION_TEST_LESSON))
                .andExpect(jsonPath(JSON_TYPE_PATH).value(THEORY_TYPE))
                .andReturn();

        LessonResponse response = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                LessonResponse.class
        );

        assertNotNull(response.id());
        assertEquals(createRequest.title(), response.title());
        assertEquals(createRequest.type(), response.type());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void getLesson_Success() throws Exception {
        // First create a lesson
        Lesson lesson = new Lesson();
        lesson.setTitle(TEST_LESSON_TITLE);
        lesson.setType(LessonType.THEORY);
        lesson.setLevel(LessonLevel.BEGINNER);
        lesson.setStatus(LessonStatus.DRAFT);
        lesson.setModule(testModule);
        lesson.setSequence(1);
        lesson = lessonRepository.save(lesson);

        mockMvc.perform(get(API_LESSONS + "/{id}", lesson.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lesson.getId()))
                .andExpect(jsonPath(JSON_TITLE_PATH).value(lesson.getTitle()))
                .andExpect(jsonPath(JSON_TYPE_PATH).value(lesson.getType().name()));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void searchLessons_Success() throws Exception {
        // Create test lesson
        Lesson lesson = new Lesson();
        lesson.setTitle(TEST_LESSON_TITLE);
        lesson.setType(LessonType.THEORY);
        lesson.setLevel(LessonLevel.BEGINNER);
        lesson.setStatus(LessonStatus.DRAFT);
        lesson.setModule(testModule);
        lesson.setSequence(1);
        lessonRepository.save(lesson);

        mockMvc.perform(get(API_LESSONS)
                        .param("type", THEORY_TYPE)
                        .param("level", "BEGINNER")
                        .param("status", "DRAFT")
                        .param("moduleId", testModule.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(lesson.getTitle()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
