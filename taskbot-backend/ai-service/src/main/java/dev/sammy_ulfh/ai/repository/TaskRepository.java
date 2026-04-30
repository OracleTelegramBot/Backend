//ai-service/src/main/java/dev/sammy_ulfh/ai/repository/TaskRepository.java
package dev.sammy_ulfh.ai.repository;

import dev.sammy_ulfh.ai.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {

    List<TaskEntity> findByStatusId(Long statusId);

    List<TaskEntity> findByPriorityId(Long priorityId);

    List<TaskEntity> findBySprintId(Long sprintId);
}