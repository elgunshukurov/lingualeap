package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.enums.UserStatus;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.request.UserUpdateRequest;
import ai.lingualeap.lingualeap.model.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface UserService {

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    Page<UserResponse> searchUsers(UserStatus status,
                                   LocalDateTime createdAtStart,
                                   LocalDateTime createdAtEnd,
                                   Pageable pageable);

    void deactivateUser(Long id);

    void updateUserStatus(Long id, UserStatus status);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
