package ai.lingualeap.lingualeap.service.impl;

import ai.lingualeap.lingualeap.dao.entity.User;
import ai.lingualeap.lingualeap.dao.repository.UserRepository;
import ai.lingualeap.lingualeap.model.request.LoginRequest;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.response.AuthResponse;
import ai.lingualeap.lingualeap.security.JwtService;
import ai.lingualeap.lingualeap.security.SecurityUser;
import ai.lingualeap.lingualeap.service.AuthenticationService;
import ai.lingualeap.lingualeap.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(new SecurityUser(savedUser));

        return AuthResponse.builder()
                .token(token)
                .username(savedUser.getUsername())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        String token = jwtService.generateToken(new SecurityUser(user));

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();
    }
}
