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
import ai.lingualeap.lingualeap.model.request.LessonUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LessonSecurityIntegrationTest extends BaseIntegrationTest {

    private static final String API_LESSONS = "/api/v1/lessons";
    private static final String TEST_DESCRIPTION = "Test Description";
    private static final String TEST_LESSON_TITLE = "Test Lesson";

    // Sabit string değerleri için yeni constantlar ekliyoruz
    private static final String PATH_ID = "/{id}";
    private static final String PATH_STATUS = "/{id}/status";
    private static final String PARAM_STATUS = "status";
    private static final String STATUS_PUBLISHED = "PUBLISHED";
    private static final String PATH_MODULE_REORDER = "/module/{moduleId}/reorder";
    private static final String PATH_PREREQUISITES = "/{id}/prerequisites/{prerequisiteId}";

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
    private Lesson testLesson;
    private LessonCreateRequest createRequest;
    private LessonUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        lessonRepository.deleteAll();
        moduleRepository.deleteAll();
        courseRepository.deleteAll();

        // Prepare test course
        Course testCourse = new Course();
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Course Description");
        testCourse.setTargetLanguage("English");
        testCourse.setSourceLanguage("Spanish");
        testCourse.setLevel(CourseLevel.A1);
        testCourse = courseRepository.save(testCourse);

        // Prepare test module
        testModule = new Module();
        testModule.setTitle("Test Module");
        testModule.setDescription(TEST_DESCRIPTION);
        testModule.setSequence(1);
        testModule.setCourse(testCourse);
        testModule = moduleRepository.save(testModule);

        // Prepare test lesson
        testLesson = new Lesson();
        testLesson.setTitle(TEST_LESSON_TITLE);
        testLesson.setType(LessonType.THEORY);
        testLesson.setLevel(LessonLevel.BEGINNER);
        testLesson.setStatus(LessonStatus.DRAFT);
        testLesson.setModule(testModule);
        testLesson.setSequence(1);
        testLesson = lessonRepository.save(testLesson);

        // Prepare create request
        createRequest = new LessonCreateRequest(
                "New Lesson",
                TEST_DESCRIPTION,
                LessonType.THEORY,
                LessonLevel.BEGINNER,
                testModule.getId(),
                2,
                70,
                30,
                "Theory content",
                false,
                null,
                Set.of(),
                Set.of()
        );

        // Prepare update request
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
    }

    // Test access for anonymous users (unauthenticated)
    @Test
    @WithAnonymousUser
    void anonymous_shouldBeForbidden_forAllEndpoints() throws Exception {
        // Create lesson - should be forbidden
        mockMvc.perform(post(API_LESSONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        // Update lesson - should be forbidden
        mockMvc.perform(put(API_LESSONS + PATH_ID, testLesson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        // Get lesson by id - should be forbidden
        mockMvc.perform(get(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isForbidden());

        // Search lessons - should be forbidden
        mockMvc.perform(get(API_LESSONS))
                .andExpect(status().isForbidden());

        // Delete lesson - should be forbidden
        mockMvc.perform(delete(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isForbidden());

        // Update status - should be forbidden
        mockMvc.perform(patch(API_LESSONS + PATH_STATUS, testLesson.getId())
                        .param(PARAM_STATUS, STATUS_PUBLISHED))
                .andExpect(status().isForbidden());
    }

    // Test access for regular users (ROLE_USER)
    @Test
    @WithMockUser(roles = "USER")
    void user_shouldHavePermission_forReadOnlyEndpoints() throws Exception {
        // Get lesson by id - should be allowed
        mockMvc.perform(get(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isOk());

        // Search lessons - should be allowed
        mockMvc.perform(get(API_LESSONS))
                .andExpect(status().isOk());

        // Create lesson - should be forbidden
        mockMvc.perform(post(API_LESSONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isForbidden());

        // Update lesson - should be forbidden
        mockMvc.perform(put(API_LESSONS + PATH_ID, testLesson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden());

        // Delete lesson - should be forbidden
        mockMvc.perform(delete(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isForbidden());

        // Update status - should be forbidden
        mockMvc.perform(patch(API_LESSONS + PATH_STATUS, testLesson.getId())
                        .param(PARAM_STATUS, STATUS_PUBLISHED))
                .andExpect(status().isForbidden());
    }

    // Test access for teacher role
    @Test
    @WithMockUser(roles = "TEACHER")
    void teacher_shouldHavePermission_forTeacherEndpoints() throws Exception {
        // Create lesson - should be allowed
        mockMvc.perform(post(API_LESSONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Update lesson - should be allowed
        mockMvc.perform(put(API_LESSONS + PATH_ID, testLesson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Get lesson by id - should be allowed
        mockMvc.perform(get(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isOk());

        // Search lessons - should be allowed
        mockMvc.perform(get(API_LESSONS))
                .andExpect(status().isOk());

        // Update status - should be allowed
        mockMvc.perform(patch(API_LESSONS + PATH_STATUS, testLesson.getId())
                        .param(PARAM_STATUS, STATUS_PUBLISHED))
                .andExpect(status().isOk());

        // Add prerequisite - should be allowed
        mockMvc.perform(post(API_LESSONS + PATH_PREREQUISITES,
                        testLesson.getId(), testLesson.getId()))
                .andExpect(status().isBadRequest()); // Bad request because same lesson can't be its own prerequisite

        // Reorder lessons - should be allowed
        Map<Long, Integer> reorderMap = new HashMap<>();
        reorderMap.put(testLesson.getId(), 3);
        mockMvc.perform(put(API_LESSONS + PATH_MODULE_REORDER, testModule.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderMap)))
                .andExpect(status().isNoContent());

        // Delete lesson - should be forbidden (admin only)
        mockMvc.perform(delete(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isForbidden());
    }

    // Test access for admin role
    @Test
    @WithMockUser(roles = "ADMIN")
    void admin_shouldHavePermission_forAllEndpoints() throws Exception {
        // Create lesson - should be allowed
        mockMvc.perform(post(API_LESSONS)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        // Update lesson - should be allowed
        mockMvc.perform(put(API_LESSONS + PATH_ID, testLesson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Get lesson by id - should be allowed
        mockMvc.perform(get(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isOk());

        // Search lessons - should be allowed
        mockMvc.perform(get(API_LESSONS))
                .andExpect(status().isOk());

        // Update status - should be allowed
        mockMvc.perform(patch(API_LESSONS + PATH_STATUS, testLesson.getId())
                        .param(PARAM_STATUS, STATUS_PUBLISHED))
                .andExpect(status().isOk());

        // Reorder lessons - should be allowed
        Map<Long, Integer> reorderMap = new HashMap<>();
        reorderMap.put(testLesson.getId(), 3);
        mockMvc.perform(put(API_LESSONS + PATH_MODULE_REORDER, testModule.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reorderMap)))
                .andExpect(status().isNoContent());

        // Delete lesson - should be allowed (admin only)
        mockMvc.perform(delete(API_LESSONS + PATH_ID, testLesson.getId()))
                .andExpect(status().isNoContent());
    }
}
