package dev.sammy_ulfh.task.controller;

import dev.sammy_ulfh.task.model.dto.CreateTaskDTO;
import dev.sammy_ulfh.task.model.dto.TaskResponseDTO;
import dev.sammy_ulfh.task.model.dto.TimeLogDTO;
import dev.sammy_ulfh.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByUser(@PathVariable Long userId) {
        log.info("GET /api/tasks/user/{}", userId);
        return ResponseEntity.ok(taskService.getTasksByUser(userId));
    }

    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksBySprint(@PathVariable Long sprintId) {
        log.info("GET /api/tasks/sprint/{}", sprintId);
        return ResponseEntity.ok(taskService.getTasksBySprint(sprintId));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody CreateTaskDTO dto) {
        log.info("POST /api/tasks");
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(dto));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam Long statusId) {
        log.info("PATCH /api/tasks/{}/status?statusId={}", taskId, statusId);
        return ResponseEntity.ok(taskService.updateTaskStatus(taskId, statusId));
    }

    @PostMapping("/{taskId}/time-logs")
    public ResponseEntity<Void> logTime(
            @PathVariable Long taskId,
            @Valid @RequestBody TimeLogDTO dto) {
        log.info("POST /api/tasks/{}/time-logs", taskId);
        taskService.logTime(taskId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<Void> assignTask(
            @PathVariable Long taskId,
            @RequestParam Long userId) {
        log.info("PATCH /api/tasks/{}/assign?userId={}", taskId, userId);
        taskService.assignTask(taskId, userId);
        return ResponseEntity.ok().build();
    }
}
