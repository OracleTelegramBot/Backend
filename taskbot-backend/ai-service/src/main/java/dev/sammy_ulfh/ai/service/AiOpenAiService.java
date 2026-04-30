package dev.sammy_ulfh.ai.service;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class AiOpenAiService {

    private final OpenAiService service;

    public AiOpenAiService(@Value("${openai.api.key}") String key) {
        this.service = new OpenAiService(key, Duration.ofSeconds(20));
    }

    public String generateResponse(String prompt) {
        try {
            ChatMessage system = new ChatMessage("system",
                    "Eres un asistente experto en gestión ágil. Responde claro y estructurado.");

            ChatMessage user = new ChatMessage("user", prompt);

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4") // 🔥 CAMBIO: mejor modelo
                    .messages(List.of(system, user))
                    .maxTokens(300) // 🔥 control costo
                    .temperature(0.3) // 🔥 más precisión
                    .build();

            ChatCompletionResult result = service.createChatCompletion(request);

            return result.getChoices().get(0).getMessage().getContent();

        } catch (Exception e) {
            throw new RuntimeException("Error IA: " + e.getMessage());
        }
    }
}