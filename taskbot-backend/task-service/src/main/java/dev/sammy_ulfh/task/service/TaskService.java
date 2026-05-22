package dev.sammy_ulfh.task.service;

import dev.sammy_ulfh.task.model.dto.CreateSprintDTO;
import dev.sammy_ulfh.task.model.dto.CreateTaskDTO;
import dev.sammy_ulfh.task.model.dto.TaskResponseDTO;
import dev.sammy_ulfh.task.model.dto.TimeLogDTO;
import dev.sammy_ulfh.task.model.entity.Sprint;

import java.util.List;

public interface TaskService {

    // Consultas
    List<TaskResponseDTO> getTasksByUser(Long userId);
    List<TaskResponseDTO> getTasksBySprint(Long sprintId);

    // Tareas
    TaskResponseDTO createTask(CreateTaskDTO dto);
    TaskResponseDTO updateTaskStatus(Long taskId, Long statusId);
    void assignTask(Long taskId, Long userId);

    // Horas
    void logTime(Long taskId, TimeLogDTO dto);

    // Sprints
    void createSprint(CreateSprintDTO dto);
    List<Sprint> getSprintsByProject(Long projectId);
}