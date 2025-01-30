package ai.lingualeap.lingualeap;

import ai.lingualeap.lingualeap.config.BaseIntegrationTest;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.enums.UserStatus;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest extends BaseIntegrationTest {

    private static final String INTEGRATION_TEST_USERNAME = "integrationtest";
    private static final String INTEGRATION_TEST_EMAIL = "integration@test.com";
    private static final String USERS_ENDPOINT = "/api/v1/users";
    private static final String USER_ENDPOINT = "/api/v1/users/{id}";
    private static final String STATUS_EXPRESSION = "$.status";
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String USER_PASSWORD = "password123";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void createAndRetrieveUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(INTEGRATION_TEST_USERNAME);
        request.setEmail(INTEGRATION_TEST_EMAIL);
        request.setPassword(USER_PASSWORD);
        request.setFirstName("Integration");
        request.setLastName("Test");

        // Create user
        MvcResult createResult = mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        assertNotNull(createResponse.getId());
        assertEquals(INTEGRATION_TEST_USERNAME, createResponse.getUsername());
        assertEquals(INTEGRATION_TEST_EMAIL, createResponse.getEmail());
        assertEquals(UserStatus.ACTIVE, createResponse.getStatus());

        // Retrieve user
        mockMvc.perform(get(USER_ENDPOINT, createResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(INTEGRATION_TEST_USERNAME))
                .andExpect(jsonPath("$.email").value(INTEGRATION_TEST_EMAIL))
                .andExpect(jsonPath(STATUS_EXPRESSION).value(ACTIVE_STATUS));
    }

    @Test
    void createDuplicateUsernameFails() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("duplicate");
        request.setEmail("first@test.com");
        request.setPassword(USER_PASSWORD);

        // Create first user
        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Try to create duplicate
        request.setEmail("second@test.com");
        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deactivateUser() throws Exception {
        // Create user first
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("todeactivate");
        request.setEmail("deactivate@test.com");
        request.setPassword(USER_PASSWORD);

        MvcResult createResult = mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        // Deactivate user
        mockMvc.perform(patch("/api/v1/users/{id}/deactivate", createResponse.getId()))
                .andExpect(status().isNoContent());

        // Verify user is deactivated
        mockMvc.perform(get(USER_ENDPOINT, createResponse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(STATUS_EXPRESSION).value("INACTIVE"));
    }

    @Test
    void searchUsers() throws Exception {
        // Create test users
        createTestUser("user1", "user1@test.com");
        createTestUser("user2", "user2@test.com");

        // Search with pagination
        mockMvc.perform(get(USERS_ENDPOINT)
                        .param("status", ACTIVE_STATUS)
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content[0].status").value(ACTIVE_STATUS));
    }

    private void createTestUser(String username, String email) throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(USER_PASSWORD);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
