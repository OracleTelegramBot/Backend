package dev.sammy_ulfh.authentication.model.dto;

public class UserRegistrationRequest {
    private String correo;
    private String password;
    private String firstName;
    private String lastName;
    private Long idRol;
    private Long idEquipo;
    
    public UserRegistrationRequest() {}
    
    public UserRegistrationRequest(String correo, String password, String firstName, String lastName, Long idRol, Long idEquipo) {
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

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }
}
