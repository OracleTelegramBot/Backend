package dev.sammy_ulfh.telegram.controller;

import dev.sammy_ulfh.telegram.client.KpiFeignClient;
import dev.sammy_ulfh.telegram.dto.telegram.TelegramResponseDTO;
import dev.sammy_ulfh.telegram.service.TelegramService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/webhook/telegram")
@Tag(name = "Conexion con Telegram API", description = "Endpoints para la conexion y manejo del administrador desde Telegram.")
public class TelegramWebhookController {

    private final TelegramService telegramService;

    @Autowired
    public TelegramWebhookController(TelegramService telegramService){
        this.telegramService = telegramService;
    }

    @PostMapping
    public ResponseEntity<String> receiveUpdate(@RequestBody Update update){
        System.out.println("Update ID recibido: " + update.getUpdateId());

        // Extrae la información y la envia al Servicio
        // Validamos que el la actualizacion nos entregue un mensaje de texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();

            // se llama a la implementación del servicio
            telegramService.handleIncomingMessage(chatId, messageText);
        }

        // respuesta de OK a telegram
        return ResponseEntity.ok("OK");
    }
}