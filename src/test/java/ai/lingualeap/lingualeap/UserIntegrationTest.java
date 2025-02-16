package ai.lingualeap.lingualeap;

import ai.lingualeap.lingualeap.config.BaseIntegrationTest;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.enums.UserStatus;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.response.AuthResponse;
import ai.lingualeap.lingualeap.model.response.UserResponse;
import ai.lingualeap.lingualeap.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class UserIntegrationTest extends BaseIntegrationTest {

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String INTEGRATION_TEST_USERNAME = "integrationtest";
    private static final String INTEGRATION_TEST_EMAIL = "integration@test.com";
    private static final String INTEGRATION_TEST_FIRSTNAME = "Integration";
    private static final String INTEGRATION_TEST_LASTNAME = "Test";
    private static final String USERS_ENDPOINT = "/api/v1/users";
    private static final String USER_ENDPOINT = "/api/v1/users/{id}";
    private static final String DEACTIVATE_ENDPOINT = "/api/v1/users/{id}/deactivate";
    private static final String USERNAME_EXPRESSION = "$.username";
    private static final String EMAIL_EXPRESSION = "$.email";
    private static final String STATUS_EXPRESSION = "$.status";
    private static final String CONTENT_EXPRESSION = "$.content[0].status";
    private static final String TOTAL_ELEMENTS_EXPRESSION = "$.totalElements";
    private static final String ACTIVE_STATUS = "ACTIVE";
    private static final String INACTIVE_STATUS = "INACTIVE";
    private static final String USER_PASSWORD = "password123";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authenticationService;

    private String authToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        UserCreateRequest adminRequest = new UserCreateRequest();
        adminRequest.setUsername(ADMIN_USERNAME);
        adminRequest.setEmail(ADMIN_EMAIL);
        adminRequest.setPassword(USER_PASSWORD);

        AuthResponse authResponse = authenticationService.register(adminRequest);
        authToken = authResponse.getToken();
    }

    @Test
    void createAndRetrieveUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(INTEGRATION_TEST_USERNAME);
        request.setEmail(INTEGRATION_TEST_EMAIL);
        request.setPassword(USER_PASSWORD);
        request.setFirstName(INTEGRATION_TEST_FIRSTNAME);
        request.setLastName(INTEGRATION_TEST_LASTNAME);

        MvcResult createResult = mockMvc.perform(post(USERS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken)
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

        mockMvc.perform(get(USER_ENDPOINT, createResponse.getId())
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath(USERNAME_EXPRESSION).value(INTEGRATION_TEST_USERNAME))
                .andExpect(jsonPath(EMAIL_EXPRESSION).value(INTEGRATION_TEST_EMAIL))
                .andExpect(jsonPath(STATUS_EXPRESSION).value(ACTIVE_STATUS));
    }

    @Test
    void createDuplicateUsernameFails() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("duplicate");
        request.setEmail("first@test.com");
        request.setPassword(USER_PASSWORD);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        request.setEmail("second@test.com");
        mockMvc.perform(post(USERS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deactivateUser() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("todeactivate");
        request.setEmail("deactivate@test.com");
        request.setPassword(USER_PASSWORD);

        MvcResult createResult = mockMvc.perform(post(USERS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        UserResponse createResponse = objectMapper.readValue(
                createResult.getResponse().getContentAsString(),
                UserResponse.class
        );

        mockMvc.perform(patch(DEACTIVATE_ENDPOINT, createResponse.getId())
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(USER_ENDPOINT, createResponse.getId())
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath(STATUS_EXPRESSION).value(INACTIVE_STATUS));
    }

    @Test
    void searchUsers() throws Exception {
        createTestUser("user1", "user1@test.com");
        createTestUser("user2", "user2@test.com");

        mockMvc.perform(get(USERS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken)
                        .param("status", ACTIVE_STATUS)
                        .param("size", "10")
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(TOTAL_ELEMENTS_EXPRESSION).value(3))
                .andExpect(jsonPath(CONTENT_EXPRESSION).value(ACTIVE_STATUS));
    }

    private void createTestUser(String username, String email) throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(USER_PASSWORD);

        mockMvc.perform(post(USERS_ENDPOINT)
                        .header(AUTHORIZATION_HEADER, BEARER_PREFIX + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
