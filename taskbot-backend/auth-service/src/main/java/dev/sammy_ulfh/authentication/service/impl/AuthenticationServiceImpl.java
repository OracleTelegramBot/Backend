package dev.sammy_ulfh.authentication.service.impl;

import dev.sammy_ulfh.authentication.model.dto.AuthRequest;
import dev.sammy_ulfh.authentication.model.dto.AuthResponse;
import dev.sammy_ulfh.authentication.model.entity.User;
import dev.sammy_ulfh.authentication.repository.UserRepository;
import dev.sammy_ulfh.authentication.security.JwtTokenProvider;
import dev.sammy_ulfh.authentication.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public AuthenticationServiceImpl(UserRepository userRepository, 
                              JwtTokenProvider jwtTokenProvider, 
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        System.out.println("Attempting login for user: " + authRequest.getCorreo());
        
        Optional<User> user = userRepository.findByEmail(authRequest.getCorreo());
        
        if (user.isEmpty()) {
            System.err.println("User not found: " + authRequest.getCorreo());
            throw new RuntimeException("Invalid email or password");
        }
        
        User foundUser = user.get();
        
        if (!passwordEncoder.matches(authRequest.getPassword(), foundUser.getPassword())) {
            System.err.println("Invalid password for user: " + authRequest.getCorreo());
            throw new RuntimeException("Invalid email or password");
        }
        
        String token = jwtTokenProvider.generateToken(
            foundUser.getEmail(), 
            foundUser.getId(), 
            foundUser.getIdRol().longValue()
        );
        
        System.out.println("User logged in successfully: " + authRequest.getCorreo());
        
        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .expiresIn(jwtExpiration)
                .correo(foundUser.getEmail())
                .idUsuario(foundUser.getId())
                .nombre(foundUser.getFirstName())
                .apellido(foundUser.getLastName())
                .idRol(foundUser.getIdRol().longValue())
                .idEquipo(foundUser.getIdEquipo())
                .build();
    }
    
    @Override
    public AuthResponse validateToken(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            String correo = jwtTokenProvider.getCorreoFromToken(token);
            Optional<User> user = userRepository.findByEmail(correo);
            
            if (user.isPresent()) {
                User foundUser = user.get();
                return AuthResponse.builder()
                        .token(token)
                        .type("Bearer")
                        .expiresIn(jwtExpiration)
                        .correo(foundUser.getEmail())
                        .idUsuario(foundUser.getId())
                        .nombre(foundUser.getFirstName())
                        .apellido(foundUser.getLastName())
                        .idRol(foundUser.getIdRol().longValue())
                        .idEquipo(foundUser.getIdEquipo())
                        .build();
            }
        }
        throw new RuntimeException("Invalid token");
    }
    
    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }
}
