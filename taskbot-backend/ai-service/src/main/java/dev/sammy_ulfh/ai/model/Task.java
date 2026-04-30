//ai-service/src/main/java/dev/sammy_ulfh/ai/model/Task.java
package dev.sammy_ulfh.ai.model;

public class Task {

    private String id;
    private String name;

    // 🔥 CAMBIO: estados más claros y útiles para IA
    // Antes: "active", "completed"
    // Ahora: permite análisis real
    private String status; // TODO, IN_PROGRESS, BLOCKED, DONE

    // 🔥 CAMBIO: en lugar de texto libre, usar referencia consistente
    private String userId;

    private int estimatedHours;
    private int actualHours;

    // 🔥 CAMBIO: normalizado para lógica de negocio
    private String priority; // HIGH, MEDIUM, LOW

    // 🔥 NUEVO: permite detectar retrasos reales
    private String deadline;

    // 🔥 NUEVO: complejidad de tarea (muy útil para IA)
    private int storyPoints;

    // Constructor vacío (necesario para Jackson)
    public Task() {
    }

    // Constructor completo
    public Task(String id, String name, String status, String userId,
            int estimatedHours, int actualHours,
            String priority, String deadline, int storyPoints) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.userId = userId;
        this.estimatedHours = estimatedHours;
        this.actualHours = actualHours;
        this.priority = priority;
        this.deadline = deadline;
        this.storyPoints = storyPoints;
    }

    // Getters y setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public int getActualHours() {
        return actualHours;
    }

    public void setActualHours(int actualHours) {
        this.actualHours = actualHours;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }
}