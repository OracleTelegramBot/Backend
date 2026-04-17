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
    public TelegramWebhookController(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @PostMapping
    public ResponseEntity<String> receiveUpdate(@RequestBody Update update) {
        System.out.println("Update ID recibido: " + update.getUpdateId());

        // Manejo de comandos de texto
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            telegramService.handleIncomingMessage(chatId, messageText);
        }
        // Manejo de clics en botones
        else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String callbackData = update.getCallbackQuery().getData();
            telegramService.handleCallbackQuery(chatId, callbackData);
        }

        return ResponseEntity.ok("OK");
    }
}