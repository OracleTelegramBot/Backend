package dev.sammy_ulfh.task.controller;

import dev.sammy_ulfh.task.model.dto.CreateSprintDTO;
import dev.sammy_ulfh.task.model.entity.Sprint;
import dev.sammy_ulfh.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sprints")
@RequiredArgsConstructor
@Slf4j
public class SprintController {

    private final TaskService taskService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Sprint>> getSprintsByProject(@PathVariable Long projectId) {
        log.info("GET /api/sprints/project/{}", projectId);
        return ResponseEntity.ok(taskService.getSprintsByProject(projectId));
    }

    @PostMapping
    public ResponseEntity<Void> createSprint(@Valid @RequestBody CreateSprintDTO dto) {
        log.info("POST /api/sprints");
        taskService.createSprint(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}