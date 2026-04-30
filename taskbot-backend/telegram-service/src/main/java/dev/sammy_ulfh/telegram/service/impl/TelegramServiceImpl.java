package dev.sammy_ulfh.telegram.service.impl;

import dev.sammy_ulfh.telegram.client.KpiFeignClient;
import dev.sammy_ulfh.telegram.dto.auth.AuthRequest;
import dev.sammy_ulfh.telegram.dto.auth.AuthResponse;
import dev.sammy_ulfh.telegram.dto.external.ActiveResourceDTO;
import dev.sammy_ulfh.telegram.dto.external.EfficiencyResponseDTO;
import dev.sammy_ulfh.telegram.dto.external.ProductivityResponseDTO;
import dev.sammy_ulfh.telegram.dto.telegram.TelegramResponseDTO;
import dev.sammy_ulfh.telegram.entity.UserSession;
import dev.sammy_ulfh.telegram.service.TelegramService;
import dev.sammy_ulfh.telegram.service.kafka.TelegramKafkaProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TelegramServiceImpl implements TelegramService {

    private final KpiFeignClient kpiClient;
    private final TelegramKafkaProducer telegramKafkaProducer;
    private final RestTemplate restTemplate;
    
    // Auth-service URL local pointer
    @Value("${auth.service.url:http://localhost:8082}/api/v1/auth/login")
    private String authServiceUrl;

    @Value("${telegram.bot.token}")
    private String botToken;
    
    // State machine storage
    private final Map<Long, UserSession> activeSessions = new ConcurrentHashMap<>();

    public TelegramServiceImpl(KpiFeignClient kpiClient, TelegramKafkaProducer telegramKafkaProducer) {
        this.kpiClient = kpiClient;
        this.telegramKafkaProducer = telegramKafkaProducer;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void handleIncomingMessage(Long chatId, String text) {
        if (text == null || text.trim().isEmpty()) return;

        UserSession session = activeSessions.get(chatId);
        
        // Restart/Start logic
        if (text.trim().equalsIgnoreCase("/start") || text.trim().equalsIgnoreCase("/login")) {
            session = new UserSession();
            activeSessions.put(chatId, session);
            sendToTelegram(chatId, "🔐 *Bienvenido al Panel de Control*\nPor favor ingresa tu correo electrónico:", null);
            return;
        }

        if (session == null) {
            session = new UserSession();
            activeSessions.put(chatId, session);
            sendToTelegram(chatId, "🔐 *Detecto que eres nuevo o tu sesión expiró.*\nPara continuar, por favor ingresa tu correo electrónico administrativo:", null);
            return;
        }

        // Login Flow
        if (session.getState() == UserSession.SessionState.AWAITING_EMAIL) {
            session.setEmail(text.trim());
            session.setState(UserSession.SessionState.AWAITING_PASSWORD);
            sendToTelegram(chatId, "Correo recibido. Ahora ingresa tu contraseña:", null);
            return;
        }

        if (session.getState() == UserSession.SessionState.AWAITING_PASSWORD) {
            String password = text.trim();
            AuthRequest authReq = new AuthRequest(session.getEmail(), password);
            try {
                ResponseEntity<AuthResponse> res = restTemplate.postForEntity(authServiceUrl, authReq, AuthResponse.class);
                if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                    AuthResponse authRes = res.getBody();
                    // Validate if user has Admin role = 1
                    if (authRes.getIdRol() != null && authRes.getIdRol() == 1L) {
                        session.setJwtToken(authRes.getToken());
                        session.setIdRol(authRes.getIdRol());
                        session.setState(UserSession.SessionState.LOGGED_IN);
                        sendToTelegram(chatId, "✅ Inicio de sesión exitoso. ¡Bienvenido Administrador " + authRes.getNombre() + "!\nUsa `/help` para ver los comandos disponibles.", null);
                    } else {
                        activeSessions.remove(chatId);
                        sendToTelegram(chatId, "❌ Acceso denegado. Este bot es de acceso exclusivo para usuarios con Rol de Administrador.", null);
                    }
                } else {
                    session.setState(UserSession.SessionState.AWAITING_EMAIL);
                    sendToTelegram(chatId, "❌ Credenciales incorrectas. Vuelve a intentar ingresando tu correo electrónico:", null);
                }
            } catch (Exception e) {
                session.setState(UserSession.SessionState.AWAITING_EMAIL);
                sendToTelegram(chatId, "❌ Credenciales incorrectas o servidor de Autenticación caído. Vuelve a ingresar tu correo electrónico:", null);
            }
            return;
        }

        // Logged-in Flow
        if (session.getState() != UserSession.SessionState.LOGGED_IN) {
            return; 
        }

        String[] parts = text.trim().toLowerCase().split("\\s+");
        String command = parts[0];
        String arg = parts.length > 1 ? parts[1] : null;

        if (command.equals("/anuncio_urgente") || command.equals("/recordatorio")) {
            String fullMessage = text.substring(command.length()).trim();
            if (fullMessage.isEmpty()) {
                sendToTelegram(chatId, "[-] Debes proveer un mensaje. Uso: " + command + " <mensaje>", null);
            } else {
                if (command.equals("/anuncio_urgente")) {
                    telegramKafkaProducer.enviarAnuncioUrgente(fullMessage);
                    sendToTelegram(chatId, "[+] Anuncio urgente enviado al equipo y persistido en BD.", null);
                } else {
                    telegramKafkaProducer.enviarRecordatorio(fullMessage);
                    sendToTelegram(chatId, "[+] Recordatorio enviado al equipo y persistido en BD.", null);
                }
            }
        } else if (command.equals("/sprint_duracion") || command.equals("/sprint_cumplimiento") ||
                command.equals("/ciclo_proyecto") || command.equals("/precision_usuario")) {

            if (arg != null) {
                try {
                    Long id = Long.parseLong(arg);
                    calcularYEnviarKpi(chatId, command, id, session.getJwtToken());
                } catch (NumberFormatException e) {
                    sendToTelegram(chatId, "[+] El ID proporcionado debe ser un número.", null);
                }
            } else {
                enviarMenu(chatId, command, session.getJwtToken());
            }
        } else if (command.equals("hola") || command.equals("/help")) {
            String helpText = "🤖 *Hola " + session.getEmail() + " - Panel de Control*\n\n" +
                    "Comandos disponibles para KPIs Ágiles:\n" +
                    "🔹 `/sprint_duracion [id]`\n" +
                    "🔹 `/sprint_cumplimiento [id]`\n" +
                    "🔹 `/ciclo_proyecto [id]`\n" +
                    "🔹 `/precision_usuario [id]`\n\n" +
                    "Comandos de Notificaciones por Kafka:\n" +
                    "🔹 `/anuncio_urgente <mensaje>`\n" +
                    "🔹 `/recordatorio <mensaje>`\n\n" +
                    "_Si omites el ID en KPIs, te mostraré un menú interactivo._";
            sendToTelegram(chatId, helpText, null);
        } else {
            sendToTelegram(chatId,
                    "[!] Comando no reconocido. Usa 'hola' o '/help' para ver las opciones disponibles.",
                    null);
        }
    }

    @Override
    public void handleCallbackQuery(Long chatId, String data) {
        if (data == null || !data.contains("_")) return;
        
        UserSession session = activeSessions.get(chatId);
        if (session == null || session.getState() != UserSession.SessionState.LOGGED_IN) {
            sendToTelegram(chatId, "⚠️ Sessión inválida. Usa `/start` para iniciar sesión y ganar permisos.", null);
            return;
        }

        int lastIndex = data.lastIndexOf("_");
        if (lastIndex > 0) {
            String command = data.substring(0, lastIndex);
            try {
                Long id = Long.parseLong(data.substring(lastIndex + 1));
                calcularYEnviarKpi(chatId, command, id, session.getJwtToken());
            } catch (NumberFormatException e) {
                sendToTelegram(chatId, "[-] Error procesando el ID desde el botón.", null);
            }
        }
    }

    private void calcularYEnviarKpi(Long chatId, String command, Long id, String jwtToken) {
        String response;
        String bearer = "Bearer " + jwtToken;
        try {
            switch (command) {
                case "/sprint_duracion":
                    EfficiencyResponseDTO duracion = kpiClient.getDuracionSprint(id, bearer);
                    response = formatearRespuesta("Duración del Sprint", duracion.getEfficiencyPercentage(),
                            duracion.getCalculationDetails(), duracion.getStatusMessage());
                    break;
                case "/sprint_cumplimiento":
                    ProductivityResponseDTO cumplimiento = kpiClient.getCumplimientoSprint(id, bearer);
                    response = formatearRespuesta("Cumplimiento del Sprint", cumplimiento.getProductivityPercentage(),
                            cumplimiento.getCalculationDetails(), cumplimiento.getStatusMessage());
                    break;
                case "/ciclo_proyecto":
                    ProductivityResponseDTO ciclo = kpiClient.getTiempoCicloProyecto(id, bearer);
                    response = formatearRespuesta("Tiempo de Ciclo por Tarea", ciclo.getProductivityPercentage(),
                            ciclo.getCalculationDetails(), ciclo.getStatusMessage());
                    break;
                case "/precision_usuario":
                    EfficiencyResponseDTO precision = kpiClient.getPrecisionEstimacionUsuario(id, bearer);
                    response = formatearRespuesta("Precisión de Estimación", precision.getEfficiencyPercentage(),
                            precision.getCalculationDetails(), precision.getStatusMessage());
                    break;
                default:
                    response = "[-] Comando desconocido.";
            }
        } catch (Exception e) {
            response = "[-] Error de cálculo. Verifique que el ID exista, el microservicio KPI esté activo y tenga acceso.";
        }
        sendToTelegram(chatId, response, null);
    }

    private String formatearRespuesta(String metrica, Double porcentaje, String calculationDetails, String statusMessage) {
        return String.format("[+] *Métrica:* %s\n[+] *Resultado:* `%.2f%%`\n[+] *Detalles:* %s\n[+] *Estado:* %s",
                metrica, porcentaje, calculationDetails != null ? calculationDetails : "N/A", statusMessage);
    }

    private void enviarMenu(Long chatId, String command, String jwtToken) {
        String bearer = "Bearer " + jwtToken;
        try {
            List<ActiveResourceDTO> items;
            String tipoRecurso;

            switch (command) {
                case "/sprint_duracion":
                case "/sprint_cumplimiento":
                    items = kpiClient.getSprintsActivos(bearer);
                    tipoRecurso = "el Sprint";
                    break;
                case "/ciclo_proyecto":
                    items = kpiClient.getProyectosActivos(bearer);
                    tipoRecurso = "el Proyecto";
                    break;
                case "/precision_usuario":
                    items = kpiClient.getUsuariosActivos(bearer);
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
            sendToTelegram(chatId, "[!] Error HTTP 403/500: No se logró recuperar la lista (Revisa si tu sesión expiró).", null);
        }
    }

    private Map<String, Object> crearTecladoInline(List<ActiveResourceDTO> items, String command) {
        List<List<Map<String, String>>> keyboard = new ArrayList<>();

        for (ActiveResourceDTO item : items) {
            List<Map<String, String>> row = new ArrayList<>();
            Map<String, String> button = new HashMap<>();

            button.put("text", item.getNombre());
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