//ai-service/src/main/java/dev/sammy_ulfh/ai/mapper/SprintDataMapper.java
package dev.sammy_ulfh.ai.mapper;

import dev.sammy_ulfh.ai.entity.TaskEntity;
import dev.sammy_ulfh.ai.entity.UserEntity;
import dev.sammy_ulfh.ai.model.SprintData;
import dev.sammy_ulfh.ai.model.Task;
import dev.sammy_ulfh.ai.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class SprintDataMapper {

        public static SprintData toSprintData(List<TaskEntity> taskEntities,
                        List<UserEntity> userEntities) {

                List<Task> tasks = taskEntities.stream()
                                .map(SprintDataMapper::toTask)
                                .collect(Collectors.toList());

                List<User> users = userEntities.stream()
                                .map(e -> toUser(e, tasks))
                                .collect(Collectors.toList());

                return new SprintData(tasks, users);
        }

        private static Task toTask(TaskEntity e) {
                Task t = new Task();
                t.setId(String.valueOf(e.getId()));
                t.setName(e.getName());
                t.setStatus(resolveStatus(e.getStatusId()));
                t.setPriority(resolvePriority(e.getPriorityId()));
                t.setEstimatedHours(e.getEstimatedHours() != null ? e.getEstimatedHours() : 0);
                t.setActualHours(e.getActualHours() != null ? e.getActualHours() : 0);
                t.setDeadline(e.getDeadline() != null ? e.getDeadline().toString() : null);
                t.setStoryPoints(e.getStoryPoints() != null ? e.getStoryPoints() : 0);
                return t;
        }

        private static User toUser(UserEntity e, List<Task> allTasks) {
                User u = new User();
                u.setId(String.valueOf(e.getId()));
                u.setName(e.getNombre() + " " + e.getApellido());
                // Las tareas asignadas se filtran por userId si tu Task las tiene
                u.setAssignedTasks(
                                allTasks.stream()
                                                .filter(t -> String.valueOf(e.getId()).equals(t.getUserId()))
                                                .collect(Collectors.toList()));
                u.setCapacityHours(40); // valor por defecto, ajusta si tienes ese dato
                u.setRole(resolveRol(e.getRolId()));
                return u;
        }

        // Mapeo de ID_ESTADO → string legible para la IA
        private static String resolveStatus(Long id) {
                if (id == null)
                        return "UNKNOWN";
                return switch (id.intValue()) {
                        case 1 -> "TODO";
                        case 2 -> "IN_PROGRESS";
                        case 3 -> "DONE";
                        case 4 -> "BLOCKED";
                        default -> "UNKNOWN";
                };
        }

        // Mapeo de ID_PRIORIDAD → string legible para la IA
        private static String resolvePriority(Long id) {
                if (id == null)
                        return "MEDIUM";
                return switch (id.intValue()) {
                        case 1 -> "LOW";
                        case 2 -> "MEDIUM";
                        case 3 -> "HIGH";
                        default -> "MEDIUM";
                };
        }

        // Mapeo de ID_ROL → string legible para la IA
        private static String resolveRol(Long id) {
                if (id == null)
                        return "DEV";
                return switch (id.intValue()) {
                        case 1 -> "ADMIN";
                        case 2 -> "PM";
                        case 3 -> "DEV";
                        case 4 -> "QA";
                        default -> "DEV";
                };
        }
}