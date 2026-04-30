//ai-service/src/main/java/dev/sammy_ulfh/ai/entity/TaskEntity.java
package dev.sammy_ulfh.ai.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "TAREA")
public class TaskEntity {

    @Id
    @Column(name = "ID_TAREA")
    private Long id;

    @Column(name = "TITULO")
    private String name;

    @Column(name = "ID_ESTADO")
    private Long statusId;

    @Column(name = "ID_PRIORIDAD")
    private Long priorityId;

    @Column(name = "TIEMPO_ESTIMADO")
    private Integer estimatedHours;

    @Column(name = "TIEMPO_REAL")
    private Integer actualHours;

    @Column(name = "FECHA_LIMITE")
    private LocalDate deadline;

    @Column(name = "COMPLEJIDAD")
    private Integer storyPoints;

    @Column(name = "ID_SPRINT")
    private Long sprintId;

    public TaskEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Integer getActualHours() {
        return actualHours;
    }

    public void setActualHours(Integer actualHours) {
        this.actualHours = actualHours;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getStoryPoints() {
        return storyPoints;
    }

    public void setStoryPoints(Integer storyPoints) {
        this.storyPoints = storyPoints;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }
}