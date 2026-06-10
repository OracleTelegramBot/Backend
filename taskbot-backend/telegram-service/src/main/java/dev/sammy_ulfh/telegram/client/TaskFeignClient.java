package dev.sammy_ulfh.telegram.client;

import dev.sammy_ulfh.telegram.dto.task.CreateSprintRequestDTO;
import dev.sammy_ulfh.telegram.dto.task.CreateTaskRequestDTO;
import dev.sammy_ulfh.telegram.dto.task.SprintDTO;
import dev.sammy_ulfh.telegram.dto.task.TaskDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "task-service", url = "${feign.client.config.task-service.url:http://task-service:8084}")
public interface TaskFeignClient {

    @GetMapping("/api/sprints/project/{projectId}")
    List<SprintDTO> getSprintsByProject(@PathVariable("projectId") Long projectId);

    @PostMapping("/api/sprints")
    void createSprint(@RequestBody CreateSprintRequestDTO dto);

    @GetMapping("/api/tasks/sprint/{sprintId}")
    List<TaskDTO> getTasksBySprint(@PathVariable("sprintId") Long sprintId);

    @GetMapping("/api/tasks/user/{userId}")
    List<TaskDTO> getTasksByUser(@PathVariable("userId") Long userId);

    @PostMapping("/api/tasks")
    TaskDTO createTask(@RequestBody CreateTaskRequestDTO dto);

    @PatchMapping("/api/tasks/{taskId}/status")
    TaskDTO updateTaskStatus(@PathVariable("taskId") Long taskId, @RequestParam("statusId") Long statusId);

    @PatchMapping("/api/tasks/{taskId}/assign")
    void assignTask(@PathVariable("taskId") Long taskId, @RequestParam("userId") Long userId);
}
