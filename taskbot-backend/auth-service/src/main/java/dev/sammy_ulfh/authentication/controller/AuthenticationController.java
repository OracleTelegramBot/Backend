package dev.sammy_ulfh.authentication.controller;

import dev.sammy_ulfh.authentication.model.dto.AuthRequest;
import dev.sammy_ulfh.authentication.model.dto.AuthResponse;
import dev.sammy_ulfh.authentication.model.dto.SetPasswordRequest;
import dev.sammy_ulfh.authentication.model.dto.UserRegistrationRequest;
import dev.sammy_ulfh.authentication.model.entity.User;
import dev.sammy_ulfh.authentication.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints para autenticación, registro y validación de tokens JWT")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario con email y contraseña, y devuelve un token JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, token JWT generado",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        System.out.println("Login request for user: " + authRequest.getCorreo());
        AuthResponse response = authenticationService.login(authRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Validar token JWT",
            description = "Verifica la validez de un token JWT y devuelve la información del usuario.",
            security = @SecurityRequirement(name = "BearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token válido",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Token inválido o expirado", content = @Content)
    })
    @PostMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken(
            @Parameter(description = "Token JWT con prefijo 'Bearer '", required = true, example = "Bearer eyJhbGci...")
            @RequestHeader("Authorization") String token) {
        System.out.println("Token validation request");
        String jwt = token.substring(7); // Remove "Bearer " prefix
        AuthResponse response = authenticationService.validateToken(jwt);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "El correo ya está registrado", content = @Content)
    })
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

    @Operation(
            summary = "Asignar / actualizar contraseña de usuario existente",
            description = "Establece (o reemplaza) la contraseña de un usuario que ya existe en la base de datos. " +
                          "El valor recibido se cifra automáticamente con BCrypt antes de persistirlo. " +
                          "Útil para usuarios pre-creados en Oracle que aún no tienen contraseña registrada."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada exitosamente",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Contraseña inválida (vacía o muy corta)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/users/{id}/password")
    public ResponseEntity<User> setPassword(
            @Parameter(description = "ID del usuario al que se le asignará la contraseña", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody SetPasswordRequest request) {
        System.out.println("Set-password request for userId: " + id);
        User updated = authenticationService.setPassword(id, request.getPassword());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Health check", description = "Verifica que el servicio de autenticación esté activo.")
    @ApiResponse(responseCode = "200", description = "Servicio operativo")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Authentication Service is running");
    }
}
