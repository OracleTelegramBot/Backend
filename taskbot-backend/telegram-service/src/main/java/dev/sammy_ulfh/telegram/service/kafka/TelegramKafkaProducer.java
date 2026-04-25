/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.sammy_ulfh.telegram.service.kafka;

import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

/**
 *
 * @author sammy
 */
@Service
public class TelegramKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public TelegramKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void enviarAnuncioUrgente(String mensaje) {
        // Envia al Topico 2, Partición 0
        kafkaTemplate.send("notificaciones-equipo", 0, null, mensaje);
    }

    public void enviarRecordatorio(String mensaje) {
        // Envia al Topico 2, Partición 1
        kafkaTemplate.send("notificaciones-equipo", 1, null, mensaje);
    }
}