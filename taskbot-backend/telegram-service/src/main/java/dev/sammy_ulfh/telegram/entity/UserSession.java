package dev.sammy_ulfh.telegram.entity;

public class UserSession {
    public enum SessionState {
        AWAITING_EMAIL,
        AWAITING_PASSWORD,
        LOGGED_IN
    }

    private SessionState state;
    private String email;
    private String jwtToken;
    private Long idRol;
    
    public UserSession() {
        this.state = SessionState.AWAITING_EMAIL;
    }
    
    public SessionState getState() {
        return state;
    }
    
    public void setState(SessionState state) {
        this.state = state;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getJwtToken() {
        return jwtToken;
    }
    
    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Long getIdRol() {
        return idRol;
    }

    public void setIdRol(Long idRol) {
        this.idRol = idRol;
    }
}
