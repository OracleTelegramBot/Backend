package dev.sammy_ulfh.authentication.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private String username;
    private String email;
    
    public AuthResponse() {}
    
    public AuthResponse(String token, String type, Long expiresIn, String username, String email) {
        this.token = token;
        this.type = type;
        this.expiresIn = expiresIn;
        this.username = username;
        this.email = email;
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String token;
        private String type = "Bearer";
        private Long expiresIn;
        private String username;
        private String email;
        
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
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        
        public AuthResponse build() {
            return new AuthResponse(token, type, expiresIn, username, email);
        }
    }
}
