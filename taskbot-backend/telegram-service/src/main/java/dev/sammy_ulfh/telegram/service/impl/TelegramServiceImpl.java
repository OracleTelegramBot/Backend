package dev.sammy_ulfh.telegram.service.impl;

import dev.sammy_ulfh.telegram.client.KpiFeignClient;
import dev.sammy_ulfh.telegram.dto.telegram.TelegramResponseDTO;
import dev.sammy_ulfh.telegram.service.TelegramService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

@Service
public class TelegramServiceImpl implements TelegramService {
    // Definir funcionalidad

    private final KpiFeignClient kpiClient;
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramServiceImpl(KpiFeignClient kpiClient){
        this.kpiClient = kpiClient;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void handleIncomingMessage(Long chatId, String text){
        String messageText = text.toLowerCase();
        String response;

        if (messageText.contains("hola")){
            response = "¡Hola! Usa /productividad o /eficiencia para ver los KPIs";
        }
        else if (messageText.contains("/productividad")){
            try{
                var data = kpiClient.getProductivity(1L);
                response = String.format("Métrica de Productividad\nResultado: %.2f%%\nEstado: %s",
                        data.getPercentage(), data.getStatus());
            } catch (Exception e){
                response = "El microservicio de los KPIs no se encuentra disponible en este momento, intente as tarde.";
            }
        }
        else if(messageText.contains("/eficiencia")){
            try{
                var data = kpiClient.getEfficiency(1L);
                response = String.format("Métrica de Eficiencia\nResultado: %.2f%%\nEstado: %s",
                        data.getPercentage(), data.getStatus());
            } catch (Exception e){
                response = "El microservicio de los KPIs no se encuentra disponible en este momento, intente as tarde.";
            }
        }
        else {
            response = "No existe ese comando. Prueba con /productividad.";
        }

        sendToTelegram(chatId, response);
    }

    private void sendToTelegram(Long chatId, String text){
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        TelegramResponseDTO payload = new TelegramResponseDTO(chatId, text, "Markdown");

        try {
            restTemplate.postForEntity(url, payload, String.class);
        } catch (Exception e){
            System.err.println("Error al enviar mensaje a Telegram " + e.getMessage());
        }
    }
}
