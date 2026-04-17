package dev.sammy_ulfh.telegram.service.impl;

import dev.sammy_ulfh.telegram.client.KpiFeignClient;
import dev.sammy_ulfh.telegram.dto.external.ActiveResourceDTO;
import dev.sammy_ulfh.telegram.dto.external.EfficiencyResponseDTO;
import dev.sammy_ulfh.telegram.dto.external.ProductivityResponseDTO;
import dev.sammy_ulfh.telegram.dto.telegram.TelegramResponseDTO;
import dev.sammy_ulfh.telegram.service.TelegramService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramServiceImpl implements TelegramService {

    private final KpiFeignClient kpiClient;
    private final RestTemplate restTemplate;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramServiceImpl(KpiFeignClient kpiClient) {
        this.kpiClient = kpiClient;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void handleIncomingMessage(Long chatId, String text) {
        if (text == null || text.trim().isEmpty())
            return;

        String[] parts = text.trim().toLowerCase().split("\\s+");
        String command = parts[0];
        String arg = parts.length > 1 ? parts[1] : null;

        if (command.equals("/sprint_duracion") || command.equals("/sprint_cumplimiento") ||
                command.equals("/ciclo_proyecto") || command.equals("/precision_usuario")) {

            if (arg != null) {
                try {
                    Long id = Long.parseLong(arg);
                    calcularYEnviarKpi(chatId, command, id);
                } catch (NumberFormatException e) {
                    sendToTelegram(chatId, "[+] El ID proporcionado debe ser un número.", null);
                }
            } else {
                // Si no hay argumento, se envia el menu
                enviarMenu(chatId, command);
            }
        } else if (command.equals("/start") || command.equals("hola") || command.equals("/help")) {
            String helpText = "🤖 *Hola, soy Oracle Java Bot - Tengo mi Panel de Control*\n\n" +
                    "Comandos disponibles para KPIs Ágiles:\n" +
                    "🔹 `/sprint_duracion [id]`\n" +
                    "🔹 `/sprint_cumplimiento [id]`\n" +
                    "🔹 `/ciclo_proyecto [id]`\n" +
                    "🔹 `/precision_usuario [id]`\n\n" +
                    "_Si omites el ID en tu comando, te mostraré una lista para seleccionar._";
            sendToTelegram(chatId, helpText, null);
        } else {
            sendToTelegram(chatId,
                    "[!] Comando no reconocido. Usa 'hola', '/help' o '/start' para ver las opciones disponibles.",
                    null);
        }
    }

    @Override
    public void handleCallbackQuery(Long chatId, String data) {
        if (data == null || !data.contains("_"))
            return;

        int lastIndex = data.lastIndexOf("_");
        if (lastIndex > 0) {
            String command = data.substring(0, lastIndex);
            try {
                Long id = Long.parseLong(data.substring(lastIndex + 1));
                calcularYEnviarKpi(chatId, command, id);
            } catch (NumberFormatException e) {
                sendToTelegram(chatId, "[-] Error procesando el ID desde el botón.", null);
            }
        }
    }

    private void calcularYEnviarKpi(Long chatId, String command, Long id) {
        String response;
        try {
            switch (command) {
                case "/sprint_duracion":
                    EfficiencyResponseDTO duracion = kpiClient.getDuracionSprint(id);
                    response = formatearRespuesta("Duración del Sprint", duracion.getEfficiencyPercentage(),
                            duracion.getStatusMessage());
                    break;
                case "/sprint_cumplimiento":
                    ProductivityResponseDTO cumplimiento = kpiClient.getCumplimientoSprint(id);
                    response = formatearRespuesta("Cumplimiento del Sprint", cumplimiento.getProductivityPercentage(),
                            cumplimiento.getStatusMessage());
                    break;
                case "/ciclo_proyecto":
                    ProductivityResponseDTO ciclo = kpiClient.getTiempoCicloProyecto(id);
                    response = formatearRespuesta("Tiempo de Ciclo por Tarea", ciclo.getProductivityPercentage(),
                            ciclo.getStatusMessage());
                    break;
                case "/precision_usuario":
                    EfficiencyResponseDTO precision = kpiClient.getPrecisionEstimacionUsuario(id);
                    response = formatearRespuesta("Precisión de Estimación", precision.getEfficiencyPercentage(),
                            precision.getStatusMessage());
                    break;
                default:
                    response = "[-] Comando desconocido.";
            }
        } catch (Exception e) {
            response = "[-] Error de cálculo. Verifique que el ID exista, el microservicio KPI esté activo y haya tareas registradas.";
        }
        sendToTelegram(chatId, response, null);
    }

    private String formatearRespuesta(String metrica, Double porcentaje, String statusMessage) {
        return String.format("[+] *Métrica:* %s\n[+] *Resultado:* `%.2f%%`\n[+] *Estado:* %s",
                metrica, porcentaje, statusMessage);
    }

    private void enviarMenu(Long chatId, String command) {
        try {
            List<ActiveResourceDTO> items;
            String tipoRecurso;

            switch (command) {
                case "/sprint_duracion":
                case "/sprint_cumplimiento":
                    items = kpiClient.getSprintsActivos();
                    tipoRecurso = "el Sprint";
                    break;
                case "/ciclo_proyecto":
                    items = kpiClient.getProyectosActivos();
                    tipoRecurso = "el Proyecto";
                    break;
                case "/precision_usuario":
                    items = kpiClient.getUsuariosActivos();
                    tipoRecurso = "el Desarrollador";
                    break;
                default:
                    sendToTelegram(chatId, "[!] Comando no reconocido para generar menú.", null);
                    return;
            }

            if (items == null || items.isEmpty()) {
                sendToTelegram(chatId, "[-] No hay registros activos disponibles en la base de datos.", null);
                return;
            }

            Map<String, Object> replyMarkup = crearTecladoInline(items, command);
            sendToTelegram(chatId, "Selecciona " + tipoRecurso + " para calcular *" + command + "*:", replyMarkup);

        } catch (Exception e) {
            sendToTelegram(chatId, "[!] Error: No se logró recuperar la lista de elementos desde el KPI Service.",
                    null);
        }
    }

    private Map<String, Object> crearTecladoInline(List<ActiveResourceDTO> items, String command) {
        List<List<Map<String, String>>> keyboard = new ArrayList<>();

        for (ActiveResourceDTO item : items) {
            List<Map<String, String>> row = new ArrayList<>();
            Map<String, String> button = new HashMap<>();

            button.put("text", item.getNombre());
            // formato: /comando_id
            button.put("callback_data", command + "_" + item.getId());

            row.add(button);
            keyboard.add(row);
        }

        Map<String, Object> replyMarkup = new HashMap<>();
        replyMarkup.put("inline_keyboard", keyboard);
        return replyMarkup;
    }

    private void sendToTelegram(Long chatId, String text, Object replyMarkup) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        TelegramResponseDTO payload = new TelegramResponseDTO();
        payload.setChat_id(chatId);
        payload.setText(text);
        payload.setParse_mode("Markdown");

        if (replyMarkup != null) {
            payload.setReply_markup(replyMarkup);
        }

        try {
            restTemplate.postForEntity(url, payload, String.class);
        } catch (Exception e) {
            System.err.println("Error enviando petición a la API de Telegram: " + e.getMessage());
        }
    }
}