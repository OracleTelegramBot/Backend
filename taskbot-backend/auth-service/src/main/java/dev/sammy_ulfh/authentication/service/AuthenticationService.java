package dev.sammy_ulfh.authentication.service;

import dev.sammy_ulfh.authentication.model.dto.AuthRequest;
import dev.sammy_ulfh.authentication.model.dto.AuthResponse;
import dev.sammy_ulfh.authentication.model.entity.User;

public interface AuthenticationService {
    AuthResponse login(AuthRequest authRequest);
    AuthResponse validateToken(String token);
    User createUser(User user);
}
