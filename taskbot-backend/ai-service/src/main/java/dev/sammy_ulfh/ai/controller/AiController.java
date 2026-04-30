//ai-service/src/main/java/dev/sammy_ulfh/ai/controller/AiController.java
package dev.sammy_ulfh.ai.controller;

import dev.sammy_ulfh.ai.dto.AiAnalysisResponse;
import dev.sammy_ulfh.ai.dto.ChatRequest; 
import dev.sammy_ulfh.ai.model.SprintData;
import dev.sammy_ulfh.ai.service.AiAnalysisService;
import dev.sammy_ulfh.ai.service.AiChatService;
import dev.sammy_ulfh.ai.service.DataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiAnalysisService aiAnalysisService;

    @Autowired
    private AiChatService aiChatService;

    @Autowired
    private DataService dataService;

    @GetMapping("/health")
    public String health() {
        return "AI Service is running!";
    }

    // 🔥 CAMBIO: ya no guarda contexto global (stateless)
    @PostMapping("/analyze-dashboard")
    public AiAnalysisResponse analyzeDashboard(@RequestBody SprintData sprintData) {
        return aiAnalysisService.generateRecommendations(sprintData);
    }

    @PostMapping("/chat")
    public String chat(@RequestParam Long userId,
                       @RequestParam Long sprintId,
                       @RequestBody ChatRequest request) {
        return aiChatService.chat(request.getMessage(), userId, sprintId);
    }

    @PostMapping("/analyze-from-db")
    public AiAnalysisResponse analyzeFromDB() {

        SprintData sprintData = dataService.buildSprintData();

        return aiAnalysisService.generateRecommendations(sprintData);
    }
}