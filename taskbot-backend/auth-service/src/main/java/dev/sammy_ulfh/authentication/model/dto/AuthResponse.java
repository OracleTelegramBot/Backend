package dev.sammy_ulfh.authentication.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private String correo;
    
    // Additional Data for Frontend / Dashboard
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private Long idRol;
    
    public AuthResponse() {}
    
    public AuthResponse(String token, String type, Long expiresIn, String correo, Long idUsuario, String nombre, String apellido, Long idRol) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
        this.correo = correo;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.idRol = idRol;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @JsonProperty("expires_in")
    public Long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getCorreo() {
        return correo;
    }
    
    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String token;
        private String type = "Bearer";
        private Long expiresIn;
        private String correo;
        private Long idUsuario;
        private String nombre;
        private String apellido;
        private Long idRol;
        
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        
        public Builder type(String type) {
            this.type = type;
            return this;
        }
        
        public Builder expiresIn(Long expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }
        
        public Builder correo(String correo) {
            this.correo = correo;
            return this;
        }
        
        public Builder idUsuario(Long idUsuario) {
            this.idUsuario = idUsuario;
            return this;
        }
        
        public Builder nombre(String nombre) {
            this.nombre = nombre;
            return this;
        }
        
        public Builder apellido(String apellido) {
            this.apellido = apellido;
            return this;
        }
        
        public Builder idRol(Long idRol) {
            this.idRol = idRol;
            return this;
        }
        
        public AuthResponse build() {
            return new AuthResponse(token, type, expiresIn, correo, idUsuario, nombre, apellido, idRol);
        }
    }
}
