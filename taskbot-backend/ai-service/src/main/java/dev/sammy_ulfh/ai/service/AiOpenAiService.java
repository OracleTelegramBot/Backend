package dev.sammy_ulfh.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiOpenAiService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.max-output-tokens:1024}")
    private int maxOutputTokens;

    @Value("${gemini.temperature:0.7}")
    private double temperature;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateResponse(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", prompt)))
                ),
                "generationConfig", Map.of(
                    "maxOutputTokens", maxOutputTokens,
                    "temperature", temperature
                )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            List<Map<String, Object>> candidates =
                (List<Map<String, Object>>) response.getBody().get("candidates");
            Map<String, Object> content =
                (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts =
                (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            throw new RuntimeException("Error Gemini: " + e.getMessage());
        }
    }
}
