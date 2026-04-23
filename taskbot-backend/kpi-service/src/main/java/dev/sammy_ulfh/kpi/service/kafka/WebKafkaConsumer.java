/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.sammy_ulfh.kpi.service.kafka;

import dev.sammy_ulfh.kpi.model.entity.AnuncioEntity;
import dev.sammy_ulfh.kpi.repository.AnuncioRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 *
 * @author sammy
 */
@Service
public class WebKafkaConsumer {

    private final SimpMessagingTemplate webSocket;
    private final AnuncioRepository anuncioRepository;

    public WebKafkaConsumer(SimpMessagingTemplate webSocket, AnuncioRepository anuncioRepository) {
        this.webSocket = webSocket;
        this.anuncioRepository = anuncioRepository;
    }

    // Escucha la particion 0 (Urgentes)
    @KafkaListener(groupId = "web-workers-group", topicPartitions = @TopicPartition(topic = "notificaciones-equipo", partitions = {
            "0" }))
    public void recibirUrgente(String mensaje) {
        AnuncioEntity anuncio = AnuncioEntity.builder()
                .mensaje(mensaje)
                .tipo("URGENTE")
                .fecha(new Date())
                .build();

        anuncioRepository.save(anuncio);
        // Se envia al Frontend por el canal de alertas
        webSocket.convertAndSend("/topic/alertas", "[URGENTE] " + mensaje);
    }

    // Escucha la particion 1 (recordatorios)
    @KafkaListener(groupId = "web-workers-group", topicPartitions = @TopicPartition(topic = "notificaciones-equipo", partitions = {
            "1" }))
    public void recibirRecordatorio(String mensaje) {

        AnuncioEntity anuncio = AnuncioEntity.builder()
                .mensaje(mensaje)
                .tipo("RECORDATORIO")
                .fecha(new Date())
                .build();

        anuncioRepository.save(anuncio);

        // Se envia al Frontend por el canal de recordatorios
        webSocket.convertAndSend("/topic/recordatorios", mensaje);
    }
}