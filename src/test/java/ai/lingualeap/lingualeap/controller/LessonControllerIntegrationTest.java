package ai.lingualeap.lingualeap.controller;

import ai.lingualeap.lingualeap.config.BaseIntegrationTest;
import ai.lingualeap.lingualeap.dao.entity.Lesson;
import ai.lingualeap.lingualeap.dao.entity.Module;
import ai.lingualeap.lingualeap.dao.repository.LessonRepository;
import ai.lingualeap.lingualeap.dao.repository.ModuleRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LessonControllerIntegrationTest extends BaseIntegrationTest {

    private static final String API_LESSONS = "/api/v1/lessons";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    private Module testModule;
    private LessonCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        lessonRepository.deleteAll();
        moduleRepository.deleteAll();

        testModule = new Module();
        testModule.setTitle("Test Module");
        testModule.setDescription("Test Description");
        testModule.setSequence(1);
        testModule = moduleRepository.save(testModule);

        createRequest = new LessonCreateRequest(
                "Integration Test Lesson",
                "Test Description",
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
                .andExpect(jsonPath("$.title").value("Integration Test Lesson"))
                .andExpect(jsonPath("$.type").value("THEORY"))
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
        lesson.setTitle("Test Lesson");
        lesson.setType(LessonType.THEORY);
        lesson.setLevel(LessonLevel.BEGINNER);
        lesson.setStatus(LessonStatus.DRAFT);
        lesson.setModule(testModule);
        lesson = lessonRepository.save(lesson);

        mockMvc.perform(get(API_LESSONS + "/{id}", lesson.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lesson.getId()))
                .andExpect(jsonPath("$.title").value(lesson.getTitle()))
                .andExpect(jsonPath("$.type").value(lesson.getType().name()));
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void searchLessons_Success() throws Exception {
        // Create test lesson
        Lesson lesson = new Lesson();
        lesson.setTitle("Test Lesson");
        lesson.setType(LessonType.THEORY);
        lesson.setLevel(LessonLevel.BEGINNER);
        lesson.setStatus(LessonStatus.DRAFT);
        lesson.setModule(testModule);
        lessonRepository.save(lesson);

        mockMvc.perform(get(API_LESSONS)
                        .param("type", "THEORY")
                        .param("level", "BEGINNER")
                        .param("status", "DRAFT")
                        .param("moduleId", testModule.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(lesson.getTitle()))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
