package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.enums.UserStatus;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.response.UserResponse;
import ai.lingualeap.lingualeap.service.impl.UserServiceImpl;
import ai.lingualeap.lingualeap.service.mapper.UserMapper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String USER_NAME = "testuser";
    private static final String EMAIL_ADDRESS = "test@example.com";
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateRequest createRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(USER_NAME);
        testUser.setEmail(EMAIL_ADDRESS);
        testUser.setStatus(UserStatus.ACTIVE);

        createRequest = new UserCreateRequest();
        createRequest.setUsername(USER_NAME);
        createRequest.setEmail(EMAIL_ADDRESS);
        createRequest.setPassword("password123");

        userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setUsername(USER_NAME);
        userResponse.setEmail(EMAIL_ADDRESS);
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userMapper.toEntity(createRequest)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        UserResponse result = userService.createUser(createRequest);

        assertNotNull(result);
        assertEquals(USER_NAME, result.getUsername());
        assertEquals(EMAIL_ADDRESS, result.getEmail());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateUsername() {
        when(userRepository.existsByUsername(USER_NAME)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                userService.createUser(createRequest)
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(USER_NAME, result.getUsername());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                userService.getUserById(99L)
        );
    }

    @Test
    void searchUsers_Success() {
        Page<User> userPage = new PageImpl<>(List.of(testUser));
        when(userRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(userPage);
        when(userMapper.toResponse(testUser)).thenReturn(userResponse);

        Page<UserResponse> result = userService.searchUsers(
                UserStatus.ACTIVE,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(USER_NAME, result.getContent().get(0).getUsername());
    }

    @Test
    void deactivateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deactivateUser(1L);

        assertEquals(UserStatus.INACTIVE, testUser.getStatus());
        verify(userRepository).findById(1L);
    }
}
