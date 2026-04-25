package dev.sammy_ulfh.authentication.service;

import dev.sammy_ulfh.authentication.model.dto.AuthRequest;
import dev.sammy_ulfh.authentication.model.dto.AuthResponse;
import dev.sammy_ulfh.authentication.model.entity.User;
import dev.sammy_ulfh.authentication.repository.UserRepository;
import dev.sammy_ulfh.authentication.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public AuthenticationService(UserRepository userRepository, 
                              JwtTokenProvider jwtTokenProvider, 
                              PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.jwtTokenProvider = jwtTokenProvider;
    this.passwordEncoder = passwordEncoder;
}

    public AuthResponse login(AuthRequest authRequest) {
        System.out.println("Attempting login for user: " + authRequest.getUsername());
        
        Optional<User> user = userRepository.findByUsername(authRequest.getUsername());
        
        if (user.isEmpty()) {
            System.err.println("User not found: " + authRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
        
        User foundUser = user.get();
        
        if (!foundUser.getIsActive()) {
            System.err.println("User account is inactive: " + authRequest.getUsername());
            throw new RuntimeException("User account is inactive");
        }
        
        if (!passwordEncoder.matches(authRequest.getPassword(), foundUser.getPassword())) {
            System.err.println("Invalid password for user: " + authRequest.getUsername());
            throw new RuntimeException("Invalid username or password");
        }
        
        String token = jwtTokenProvider.generateToken(foundUser.getUsername());
        
        System.out.println("User logged in successfully: " + authRequest.getUsername());
        
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtExpiration)
                .username(foundUser.getUsername())
                .email(foundUser.getEmail())
                .build();
    }
    
    public AuthResponse validateToken(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            Optional<User> user = userRepository.findByUsername(username);
            
            if (user.isPresent()) {
                User foundUser = user.get();
                return AuthResponse.builder()
                        .token(token)
                        .type("Bearer")
                        .expiresIn(jwtExpiration)
                        .username(foundUser.getUsername())
                        .email(foundUser.getEmail())
                        .build();
            }
        }
        throw new RuntimeException("Invalid token");
    }
    
    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setCreatedAt(System.currentTimeMillis());
        user.setUpdatedAt(System.currentTimeMillis());
        
        return userRepository.save(user);
    }
}
