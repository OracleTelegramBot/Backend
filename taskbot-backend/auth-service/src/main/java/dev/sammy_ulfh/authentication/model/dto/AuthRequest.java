package dev.sammy_ulfh.authentication.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciales de inicio de sesión")
public class AuthRequest {
    @Schema(description = "Correo electrónico del usuario", example = "usuario@empresa.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correo;
    @Schema(description = "Contraseña del usuario", example = "MiPassword123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    
    public AuthRequest() {}
    
    public AuthRequest(String correo, String password) {
        this.correo = correo;
        this.password = password;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
