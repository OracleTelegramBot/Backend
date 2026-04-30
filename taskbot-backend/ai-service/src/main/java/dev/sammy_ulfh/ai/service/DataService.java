//ai-service/src/main/java/dev/sammy_ulfh/ai/service/DataService.java
package dev.sammy_ulfh.ai.service;

import dev.sammy_ulfh.ai.entity.TaskEntity;
import dev.sammy_ulfh.ai.entity.UserEntity;
import dev.sammy_ulfh.ai.mapper.SprintDataMapper;
import dev.sammy_ulfh.ai.model.SprintData;
import dev.sammy_ulfh.ai.repository.TaskRepository;
import dev.sammy_ulfh.ai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public SprintData buildSprintData() {
        List<TaskEntity> tasks = taskRepository.findAll();
        List<UserEntity> users = userRepository.findAll();
        return SprintDataMapper.toSprintData(tasks, users);
    }

    public SprintData buildSprintData(Long userId, Long sprintId) {
        List<TaskEntity> tasks = taskRepository.findBySprintId(sprintId);
        List<UserEntity> users = List.of();
        if (userId != null) {
            UserEntity user = userRepository.findById(userId).orElse(null);
            if (user != null) users = List.of(user);
        }
        return SprintDataMapper.toSprintData(tasks, users);
    }
}