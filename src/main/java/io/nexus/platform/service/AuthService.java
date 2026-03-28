package io.nexus.platform.service;

import io.nexus.platform.dto.request.LoginRequest;
import io.nexus.platform.dto.request.RegisterRequest;
import io.nexus.platform.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}