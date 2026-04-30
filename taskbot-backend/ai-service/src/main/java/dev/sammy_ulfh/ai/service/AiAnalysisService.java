package dev.sammy_ulfh.ai.service;

import dev.sammy_ulfh.ai.dto.AiAnalysisResponse;
import dev.sammy_ulfh.ai.model.SprintData;
import dev.sammy_ulfh.ai.model.Task;
import dev.sammy_ulfh.ai.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiAnalysisService {

    @Autowired
    private AiOpenAiService aiProviderService; // luego lo cambias a OCI

    public AiAnalysisResponse generateRecommendations(SprintData sprintData) {

        StringBuilder context = new StringBuilder();

        List<String> problemas = new ArrayList<>();
        List<String> riesgos = new ArrayList<>();

        context.append("=== ANÁLISIS DE TAREAS ===\n");

        for (Task task : sprintData.getActiveTasks()) {

            double desviacion = task.getActualHours() - task.getEstimatedHours();
            double porcentaje = task.getEstimatedHours() == 0 ? 0 : (desviacion / task.getEstimatedHours());

            context.append("- ").append(task.getName())
                    .append(" | Est: ").append(task.getEstimatedHours())
                    .append("h | Real: ").append(task.getActualHours())
                    .append("h | Desv: ").append(desviacion)
                    .append("h (").append((int)(porcentaje * 100)).append("%)")
                    .append("\n");

            // 🔥 REGLAS REALES
            if (porcentaje > 0.3) {
                problemas.add("Sobrecarga en tarea: " + task.getName());
            }

            if (task.getPriority().equalsIgnoreCase("HIGH") && porcentaje > 0) {
                riesgos.add("Tarea crítica retrasada: " + task.getName());
            }
        }

        context.append("\n=== CARGA DE USUARIOS ===\n");

        double totalCarga = 0;

        for (User user : sprintData.getUsers()) {

            double carga = user.getTotalActualHours();
            totalCarga += carga;

            context.append("- ").append(user.getName())
                    .append(" | Est: ").append(user.getTotalEstimatedHours())
                    .append("h | Real: ").append(carga)
                    .append("h\n");

            if (carga > 40) {
                problemas.add("Usuario sobrecargado: " + user.getName());
            }
        }

        // 🔥 Nivel de carga general
        String cargaGeneral = totalCarga > 120 ? "ALTA" : totalCarga > 60 ? "MEDIA" : "BAJA";

        // 🔥 Prompt PRO
        String prompt = """
Eres un asistente experto en gestión ágil de proyectos.

El sistema ya analizó los datos y detectó lo siguiente:

PROBLEMAS:
%s

RIESGOS:
%s

DATOS:
%s

Tu tarea:
1. Explicar brevemente los problemas
2. Priorizar las tareas críticas
3. Dar recomendaciones accionables (no genéricas)
4. Sugerir qué hacer a continuación

Responde en formato:

RECOMENDACIONES:
- ...
""".formatted(
                String.join("\n", problemas),
                String.join("\n", riesgos),
                context.toString()
        );

        String respuestaIA = aiProviderService.generateResponse(prompt);

        // 🔥 Convertimos salida en lista simple
        List<String> recomendaciones = List.of(respuestaIA.split("\n"));

        return new AiAnalysisResponse(
                problemas,
                riesgos,
                recomendaciones,
                cargaGeneral
        );
    }
}