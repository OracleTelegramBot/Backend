package dev.sammy_ulfh.authentication.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos para registrar un nuevo usuario")
public class UserRegistrationRequest {
    @Schema(description = "Correo electrónico (será el nombre de usuario)", example = "nuevo@empresa.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String correo;
    @Schema(description = "Contraseña en texto plano (se cifrará con BCrypt)", example = "Password123!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "Nombre(s) del usuario", example = "Carlos")
    private String firstName;
    @Schema(description = "Apellido(s) del usuario", example = "García")
    private String lastName;
    @Schema(description = "ID del rol asignado (ver tabla ROL)", example = "2")
    private Integer idRol;
    @Schema(description = "ID del equipo al que pertenece (ver tabla EQUIPO)", example = "1")
    private Long idEquipo;
    
    public UserRegistrationRequest() {}
    
    public UserRegistrationRequest(String correo, String password, String firstName, String lastName, Integer idRol, Long idEquipo) {
        this.correo = correo;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idRol = idRol;
        this.idEquipo = idEquipo;
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
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }
}
