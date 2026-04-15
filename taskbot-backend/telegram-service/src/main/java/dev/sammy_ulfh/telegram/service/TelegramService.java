package dev.sammy_ulfh.telegram.service;

public interface TelegramService {
    // LLamado a la funcionalidad de los DTO y las funciones definidas en ServiceIMPL
    void handleIncomingMessage(Long chatId, String text);
}
