package dev.sammy_ulfh.authentication.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload para asignar o actualizar la contraseña de un usuario existente")
public class SetPasswordRequest {

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(
        description = "Nueva contraseña en texto plano (se cifrará con BCrypt antes de persistir)",
        example = "NuevaPass123!",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    public SetPasswordRequest() {}

    public SetPasswordRequest(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
