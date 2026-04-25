package dev.sammy_ulfh.telegram.controller;

import dev.sammy_ulfh.telegram.service.kafka.TelegramKafkaProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/anuncios")
public class AnuncioController {

    private final TelegramKafkaProducer telegramKafkaProducer;

    public AnuncioController(TelegramKafkaProducer telegramKafkaProducer) {
        this.telegramKafkaProducer = telegramKafkaProducer;
    }

    @PostMapping("/urgente")
    public ResponseEntity<Void> enviarUrgente(@RequestBody Map<String, String> payload) {
        String mensaje = payload.get("mensaje");
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            telegramKafkaProducer.enviarAnuncioUrgente(mensaje);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/recordatorio")
    public ResponseEntity<Void> enviarRecordatorio(@RequestBody Map<String, String> payload) {
        String mensaje = payload.get("mensaje");
        if (mensaje != null && !mensaje.trim().isEmpty()) {
            telegramKafkaProducer.enviarRecordatorio(mensaje);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
