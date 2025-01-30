package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.enums.UserStatus;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.request.UserUpdateRequest;
import ai.lingualeap.lingualeap.model.response.UserResponse;
import ai.lingualeap.lingualeap.service.UserService;
import ai.lingualeap.lingualeap.service.mapper.UserMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final String USERNAME_EXISTS_ERROR_MESSAGE = "Username already exists: ";
    private static final String EMAIL_EXISTS_ERROR_MESSAGE = "Email already exists: ";
    private static final String CREATED_AT = "createdAt";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(USERNAME_EXISTS_ERROR_MESSAGE + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(EMAIL_EXISTS_ERROR_MESSAGE + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = getUserEntityById(id);

        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(USERNAME_EXISTS_ERROR_MESSAGE + request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(EMAIL_EXISTS_ERROR_MESSAGE + request.getEmail());
        }

        userMapper.updateEntityFromRequest(request, user);
        return userMapper.toResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(getUserEntityById(id));
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    @Override
    public Page<UserResponse> searchUsers(UserStatus status,
                                          LocalDateTime createdAtStart,
                                          LocalDateTime createdAtEnd,
                                          Pageable pageable) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size cannot be greater than " + MAX_PAGE_SIZE);
        }

        Specification<User> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (createdAtStart != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get(CREATED_AT), createdAtStart));
        }

        if (createdAtEnd != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get(CREATED_AT), createdAtEnd));
        }

        return userRepository.findAll(spec, pageable).map(userMapper::toResponse);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = getUserEntityById(id);
        user.setStatus(UserStatus.INACTIVE);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long id, UserStatus status) {
        User user = getUserEntityById(id);
        user.setStatus(status);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }
}
