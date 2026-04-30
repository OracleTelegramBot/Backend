package dev.sammy_ulfh.ai.service;

import dev.sammy_ulfh.ai.model.SprintData;
import dev.sammy_ulfh.ai.model.Task;
import dev.sammy_ulfh.ai.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    @Autowired
    private AiOpenAiService aiService;

    @Autowired
    private DataService dataService;

    public String chat(String message, Long userId, Long sprintId) {
        SprintData sprintData = dataService.buildSprintData(userId, sprintId);
        String context = buildContext(sprintData, userId);

        String prompt = """
Eres un asistente experto en gestión de proyectos ágiles.

Contexto del sprint:
%s

Pregunta:
%s
""".formatted(context, message);

        return aiService.generateResponse(prompt);
    }

    private String buildContext(SprintData sprintData, Long userId) {
        if (sprintData == null) return "Sin datos.";

        StringBuilder ctx = new StringBuilder();
        String userIdStr = userId != null ? String.valueOf(userId) : null;

        if (sprintData.getUsers() != null && !sprintData.getUsers().isEmpty()) {
            User user = sprintData.getUsers().get(0);
            ctx.append("Usuario: ").append(user.getName())
               .append(" (Rol: ").append(user.getRole()).append(")\n\n");
        }

        if (sprintData.getActiveTasks() != null && !sprintData.getActiveTasks().isEmpty()) {
            ctx.append("Tareas del sprint:\n");
            for (Task task : sprintData.getActiveTasks()) {
                boolean isOwn = userIdStr != null && userIdStr.equals(task.getUserId());
                ctx.append("- [").append(task.getStatus()).append("] ")
                   .append(task.getName())
                   .append(" | Prioridad: ").append(task.getPriority())
                   .append(" | Est: ").append(task.getEstimatedHours()).append("h")
                   .append(", Real: ").append(task.getActualHours()).append("h");
                if (task.getDeadline() != null) {
                    ctx.append(", Deadline: ").append(task.getDeadline());
                }
                if (isOwn) ctx.append(" ← tuya");
                ctx.append("\n");
            }
        } else {
            ctx.append("Sin tareas registradas en este sprint.\n");
        }

        return ctx.toString();
    }
}