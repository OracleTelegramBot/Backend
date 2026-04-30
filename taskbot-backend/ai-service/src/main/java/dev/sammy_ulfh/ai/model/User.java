//ai-service/src/main/java/dev/sammy_ulfh/ai/model/User.java
package dev.sammy_ulfh.ai.model;

import java.util.List;

public class User {

    private String id;
    private String name;

    // 🔥 CAMBIO: sigue útil para contexto IA
    private List<Task> assignedTasks;

    // 🔥 NUEVO: capacidad real del usuario
    private int capacityHours; // ej: 40h por sprint

    // 🔥 NUEVO: rol dentro del equipo
    private String role; // DEV, QA, PM

    // 🔥 OPCIONAL: puedes mantenerlos o calcularlos dinámicamente
    private int totalEstimatedHours;
    private int totalActualHours;

    // Constructor vacío
    public User() {
    }

    // Constructor completo
    public User(String id, String name, List<Task> assignedTasks,
            int capacityHours, String role,
            int totalEstimatedHours, int totalActualHours) {
        this.id = id;
        this.name = name;
        this.assignedTasks = assignedTasks;
        this.capacityHours = capacityHours;
        this.role = role;
        this.totalEstimatedHours = totalEstimatedHours;
        this.totalActualHours = totalActualHours;
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

    public List<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public void setAssignedTasks(List<Task> assignedTasks) {
        this.assignedTasks = assignedTasks;
    }

    public int getCapacityHours() {
        return capacityHours;
    }

    public void setCapacityHours(int capacityHours) {
        this.capacityHours = capacityHours;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getTotalEstimatedHours() {
        return totalEstimatedHours;
    }

    public void setTotalEstimatedHours(int totalEstimatedHours) {
        this.totalEstimatedHours = totalEstimatedHours;
    }

    public int getTotalActualHours() {
        return totalActualHours;
    }

    public void setTotalActualHours(int totalActualHours) {
        this.totalActualHours = totalActualHours;
    }
}