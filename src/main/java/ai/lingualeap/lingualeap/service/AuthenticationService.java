package ai.lingualeap.lingualeap.service;

import ai.lingualeap.lingualeap.model.request.LoginRequest;
import ai.lingualeap.lingualeap.model.request.UserCreateRequest;
import ai.lingualeap.lingualeap.model.response.AuthResponse;

public interface AuthenticationService {
    AuthResponse register(UserCreateRequest request);
    AuthResponse login(LoginRequest request);
}
