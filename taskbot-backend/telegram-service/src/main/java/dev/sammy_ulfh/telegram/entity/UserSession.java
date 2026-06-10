package dev.sammy_ulfh.telegram.entity;

import java.util.HashMap;
import java.util.Map;

public class UserSession {

    public enum SessionState {
        AWAITING_EMAIL,
        AWAITING_PASSWORD,
        LOGGED_IN,
        // Sprint creation flow
        CREATING_SPRINT_NOMBRE,
        CREATING_SPRINT_FECHA_INICIO,
        CREATING_SPRINT_FECHA_FIN,
        CREATING_SPRINT_PROYECTO,
        // Task creation flow
        CREATING_TAREA_TITULO,
        CREATING_TAREA_DESCRIPCION,
        CREATING_TAREA_FECHA_LIMITE,
        CREATING_TAREA_TIEMPO_ESTIMADO,
        CREATING_TAREA_PROYECTO,
        CREATING_TAREA_SPRINT,
        CREATING_TAREA_PRIORIDAD,
        CREATING_TAREA_COMPLEJIDAD
    }

    private SessionState state;
    private String email;
    private String jwtToken;
    private Long idRol;
    private final Map<String, String> tempData = new HashMap<>();

    public UserSession() {
        this.state = SessionState.AWAITING_EMAIL;
    }

    public SessionState getState() { return state; }
    public void setState(SessionState state) { this.state = state; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getJwtToken() { return jwtToken; }
    public void setJwtToken(String jwtToken) { this.jwtToken = jwtToken; }

    public Long getIdRol() { return idRol; }
    public void setIdRol(Long idRol) { this.idRol = idRol; }

    public void setTempValue(String key, String value) { tempData.put(key, value); }
    public String getTempValue(String key) { return tempData.get(key); }
    public void clearTempData() { tempData.clear(); }

    public boolean isInCreationFlow() {
        return state.name().startsWith("CREATING_");
    }
}
