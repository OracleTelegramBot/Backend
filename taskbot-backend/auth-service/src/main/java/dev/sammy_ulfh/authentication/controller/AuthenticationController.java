package dev.sammy_ulfh.authentication.controller;

import dev.sammy_ulfh.authentication.model.dto.AuthRequest;
import dev.sammy_ulfh.authentication.model.dto.AuthResponse;
import dev.sammy_ulfh.authentication.model.dto.UserRegistrationRequest;
import dev.sammy_ulfh.authentication.model.entity.User;
import dev.sammy_ulfh.authentication.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    
    private final AuthenticationService authenticationService;
    
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        System.out.println("Login request for user: " + authRequest.getCorreo());
        AuthResponse response = authenticationService.login(authRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(@RequestHeader("Authorization") String token) {
        System.out.println("Token validation request");
        String jwt = token.substring(7); // Remove "Bearer " prefix
        AuthResponse response = authenticationService.validateToken(jwt);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationRequest registrationRequest) {
        System.out.println("Registration request for user: " + registrationRequest.getCorreo());
        User user = new User();
        user.setEmail(registrationRequest.getCorreo());
        user.setPassword(registrationRequest.getPassword());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setIdRol(registrationRequest.getIdRol());
        user.setIdEquipo(registrationRequest.getIdEquipo());
        
        User createdUser = authenticationService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication Service is running");
    }
}
