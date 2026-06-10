package dev.sammy_ulfh.telegram.service.impl;

import dev.sammy_ulfh.telegram.client.KpiFeignClient;
import dev.sammy_ulfh.telegram.client.TaskFeignClient;
import dev.sammy_ulfh.telegram.dto.auth.AuthRequest;
import dev.sammy_ulfh.telegram.dto.auth.AuthResponse;
import dev.sammy_ulfh.telegram.dto.external.ActiveResourceDTO;
import dev.sammy_ulfh.telegram.dto.external.EfficiencyResponseDTO;
import dev.sammy_ulfh.telegram.dto.external.ProductivityResponseDTO;
import dev.sammy_ulfh.telegram.dto.task.CreateSprintRequestDTO;
import dev.sammy_ulfh.telegram.dto.task.CreateTaskRequestDTO;
import dev.sammy_ulfh.telegram.dto.task.SprintDTO;
import dev.sammy_ulfh.telegram.dto.task.TaskDTO;
import dev.sammy_ulfh.telegram.dto.telegram.TelegramResponseDTO;
import dev.sammy_ulfh.telegram.entity.UserSession;
import dev.sammy_ulfh.telegram.repository.SessionRepository;
import dev.sammy_ulfh.telegram.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TelegramServiceImpl implements TelegramService {

    private final KpiFeignClient kpiClient;
    private final TaskFeignClient taskClient;
    private final RestTemplate restTemplate;

    @Autowired
    private SessionRepository sessionRepository;

    @Value("${auth.service.url:http://localhost:8082}/api/v1/auth/login")
    private String authServiceUrl;

    @Value("${telegram.bot.token}")
    private String botToken;

    public TelegramServiceImpl(KpiFeignClient kpiClient, TaskFeignClient taskClient) {
        this.kpiClient = kpiClient;
        this.taskClient = taskClient;
        this.restTemplate = new RestTemplate();
    }

    // ─── Entry points ────────────────────────────────────────────────────────────

    @Override
    public void handleIncomingMessage(Long chatId, String text) {
        if (text == null || text.trim().isEmpty()) return;

        UserSession session;
        try {
            session = sessionRepository.get(chatId);
        } catch (org.springframework.data.redis.RedisConnectionFailureException e) {
            System.err.println("[WARN] Redis no disponible para chatId=" + chatId + ": " + e.getMessage());
            session = null;
        } catch (org.springframework.data.redis.serializer.SerializationException e) {
            System.err.println("[ERROR] Sesión corrupta para chatId=" + chatId + ", borrando. Error: " + e.getMessage());
            sessionRepository.delete(chatId);
            session = null;
        } catch (Exception e) {
            System.err.println("[ERROR] Error inesperado al leer sesión para chatId=" + chatId + ": " + e.getMessage());
            session = null;
        }

        if (text.trim().equalsIgnoreCase("/start") || text.trim().equalsIgnoreCase("/login")) {
            session = new UserSession();
            sessionRepository.save(chatId, session);
            sendToTelegram(chatId, "🔐 *Bienvenido al Panel de Control*\nPor favor ingresa tu correo electrónico:", null);
            return;
        }

        if (session == null) {
            session = new UserSession();
            sessionRepository.save(chatId, session);
            sendToTelegram(chatId, "🔐 *Detecto que eres nuevo o tu sesión expiró.*\nPara continuar, por favor ingresa tu correo electrónico administrativo:", null);
            return;
        }

        if (session.getState() == UserSession.SessionState.AWAITING_EMAIL) {
            session.setEmail(text.trim());
            session.setState(UserSession.SessionState.AWAITING_PASSWORD);
            sessionRepository.save(chatId, session);
            sendToTelegram(chatId, "Correo recibido. Ahora ingresa tu contraseña:", null);
            return;
        }

        if (session.getState() == UserSession.SessionState.AWAITING_PASSWORD) {
            handlePasswordStep(chatId, text, session);
            return;
        }

        if (session.getState() != UserSession.SessionState.LOGGED_IN && session.isInCreationFlow()) {
            if (text.trim().equalsIgnoreCase("/cancelar")) {
                session.setState(UserSession.SessionState.LOGGED_IN);
                session.clearTempData();
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "✅ Operación cancelada.", null);
                return;
            }
            handleCreationFlow(chatId, text, session);
            return;
        }

        if (session.getState() == UserSession.SessionState.LOGGED_IN) {
            handleLoggedInCommand(chatId, text, session);
        }
    }

    @Override
    public void handleCallbackQuery(Long chatId, String data) {
        if (data == null || !data.contains("_")) return;

        UserSession session;
        try {
            session = sessionRepository.get(chatId);
        } catch (org.springframework.data.redis.serializer.SerializationException e) {
            System.err.println("[ERROR] Sesión corrupta en callback para chatId=" + chatId + ", borrando. Error: " + e.getMessage());
            sessionRepository.delete(chatId);
            session = null;
        } catch (Exception e) {
            System.err.println("[ERROR] Error al leer sesión en callback para chatId=" + chatId + ": " + e.getMessage());
            session = null;
        }

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

    // ─── Login ───────────────────────────────────────────────────────────────────

    private void handlePasswordStep(Long chatId, String text, UserSession session) {
        AuthRequest authReq = new AuthRequest(session.getEmail(), text.trim());
        try {
            ResponseEntity<AuthResponse> res = restTemplate.postForEntity(authServiceUrl, authReq, AuthResponse.class);
            if (res.getStatusCode().is2xxSuccessful() && res.getBody() != null) {
                AuthResponse authRes = res.getBody();
                if (authRes.getIdRol() != null && authRes.getIdRol() == 1L) {
                    session.setJwtToken(authRes.getToken());
                    session.setIdRol(authRes.getIdRol());
                    session.setState(UserSession.SessionState.LOGGED_IN);
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "✅ Inicio de sesión exitoso. ¡Bienvenido Administrador " + authRes.getNombre() + "!\nUsa `/help` para ver los comandos disponibles.", null);
                } else {
                    sessionRepository.delete(chatId);
                    sendToTelegram(chatId, "❌ Acceso denegado. Este bot es de acceso exclusivo para usuarios con Rol de Administrador.", null);
                }
            } else {
                session.setState(UserSession.SessionState.AWAITING_EMAIL);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "❌ Credenciales incorrectas. Vuelve a intentar ingresando tu correo electrónico:", null);
            }
        } catch (Exception e) {
            session.setState(UserSession.SessionState.AWAITING_EMAIL);
            sessionRepository.save(chatId, session);
            sendToTelegram(chatId, "❌ Credenciales incorrectas o servidor de Autenticación caído. Vuelve a ingresar tu correo electrónico:", null);
        }
    }

    // ─── Logged-in command dispatcher ────────────────────────────────────────────

    private void handleLoggedInCommand(Long chatId, String text, UserSession session) {
        String[] parts = text.trim().split("\\s+");
        String command = parts[0].toLowerCase();

        switch (command) {
            case "/crear_sprint" -> {
                session.setState(UserSession.SessionState.CREATING_SPRINT_NOMBRE);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "📋 *Crear Sprint* — Paso 1/4\nIntroduce el *nombre* del sprint:", null);
            }
            case "/sprints" -> handleSprints(chatId, parts);
            case "/crear_tarea" -> {
                session.setState(UserSession.SessionState.CREATING_TAREA_TITULO);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 1/8\nIntroduce el *título* de la tarea:", null);
            }
            case "/tareas_sprint" -> handleTareasSprint(chatId, parts);
            case "/tareas_usuario" -> handleTareasUsuario(chatId, parts);
            case "/asignar_tarea" -> handleAsignarTarea(chatId, parts);
            case "/estado_tarea" -> handleEstadoTarea(chatId, parts);
            case "/cancelar" -> sendToTelegram(chatId, "No hay ninguna operación activa para cancelar.", null);
            case "/sprint_duracion", "/sprint_cumplimiento", "/ciclo_proyecto", "/precision_usuario" ->
                handleKpiCommand(chatId, command, parts, session);
            case "hola", "/help" -> sendHelp(chatId, session);
            default -> sendToTelegram(chatId, "[!] Comando no reconocido. Usa '/help' para ver los comandos disponibles.", null);
        }
    }

    // ─── Sprint commands ─────────────────────────────────────────────────────────

    private void handleSprints(Long chatId, String[] parts) {
        if (parts.length < 2) {
            sendToTelegram(chatId, "[-] Uso: `/sprints <idProyecto>`", null);
            return;
        }
        try {
            Long proyectoId = Long.parseLong(parts[1]);
            List<SprintDTO> sprints = taskClient.getSprintsByProject(proyectoId);
            if (sprints == null || sprints.isEmpty()) {
                sendToTelegram(chatId, "[-] No hay sprints registrados para el proyecto #" + proyectoId + ".", null);
                return;
            }
            StringBuilder sb = new StringBuilder("📋 *Sprints del Proyecto #" + proyectoId + "*\n\n");
            for (SprintDTO s : sprints) {
                sb.append("🔹 `[ID: ").append(s.getIdSprint()).append("]` *").append(s.getNombre()).append("*\n");
                sb.append("   Inicio: ").append(s.getFechaInicio());
                if (s.getFechaFin() != null) sb.append(" | Fin: ").append(s.getFechaFin());
                sb.append("\n");
            }
            sendToTelegram(chatId, sb.toString(), null);
        } catch (NumberFormatException e) {
            sendToTelegram(chatId, "[-] El ID del proyecto debe ser un número.", null);
        } catch (Exception e) {
            sendToTelegram(chatId, "[-] Error al obtener sprints: " + e.getMessage(), null);
        }
    }

    // ─── Task commands ───────────────────────────────────────────────────────────

    private void handleTareasSprint(Long chatId, String[] parts) {
        if (parts.length < 2) {
            sendToTelegram(chatId, "[-] Uso: `/tareas_sprint <idSprint>`", null);
            return;
        }
        try {
            Long sprintId = Long.parseLong(parts[1]);
            List<TaskDTO> tareas = taskClient.getTasksBySprint(sprintId);
            sendToTelegram(chatId, formatearListaTareas("Sprint #" + sprintId, tareas), null);
        } catch (NumberFormatException e) {
            sendToTelegram(chatId, "[-] El ID del sprint debe ser un número.", null);
        } catch (Exception e) {
            sendToTelegram(chatId, "[-] Error al obtener tareas: " + e.getMessage(), null);
        }
    }

    private void handleTareasUsuario(Long chatId, String[] parts) {
        if (parts.length < 2) {
            sendToTelegram(chatId, "[-] Uso: `/tareas_usuario <idUsuario>`", null);
            return;
        }
        try {
            Long userId = Long.parseLong(parts[1]);
            List<TaskDTO> tareas = taskClient.getTasksByUser(userId);
            sendToTelegram(chatId, formatearListaTareas("Usuario #" + userId, tareas), null);
        } catch (NumberFormatException e) {
            sendToTelegram(chatId, "[-] El ID del usuario debe ser un número.", null);
        } catch (Exception e) {
            sendToTelegram(chatId, "[-] Error al obtener tareas: " + e.getMessage(), null);
        }
    }

    private void handleAsignarTarea(Long chatId, String[] parts) {
        if (parts.length < 3) {
            sendToTelegram(chatId, "[-] Uso: `/asignar_tarea <idTarea> <idUsuario>`", null);
            return;
        }
        try {
            Long tareaId = Long.parseLong(parts[1]);
            Long userId = Long.parseLong(parts[2]);
            taskClient.assignTask(tareaId, userId);
            sendToTelegram(chatId, "[+] Tarea #" + tareaId + " asignada correctamente al usuario #" + userId + ".", null);
        } catch (NumberFormatException e) {
            sendToTelegram(chatId, "[-] Los IDs deben ser números. Uso: `/asignar_tarea <idTarea> <idUsuario>`", null);
        } catch (Exception e) {
            sendToTelegram(chatId, "[-] Error al asignar la tarea: " + e.getMessage(), null);
        }
    }

    private void handleEstadoTarea(Long chatId, String[] parts) {
        if (parts.length < 3) {
            sendToTelegram(chatId, "[-] Uso: `/estado_tarea <idTarea> <idEstado>`\n_Estados: 1=TODO, 2=EN PROGRESO, 3=COMPLETADA_", null);
            return;
        }
        try {
            Long tareaId = Long.parseLong(parts[1]);
            Long estadoId = Long.parseLong(parts[2]);
            if (estadoId < 1 || estadoId > 3) {
                sendToTelegram(chatId, "[-] Estado inválido. Usa: 1=TODO, 2=EN PROGRESO, 3=COMPLETADA", null);
                return;
            }
            TaskDTO tarea = taskClient.updateTaskStatus(tareaId, estadoId);
            sendToTelegram(chatId, "[+] Tarea #" + tarea.getIdTarea() + " *" + tarea.getTitulo() + "* actualizada a *" + estadoLabel(estadoId) + "*.", null);
        } catch (NumberFormatException e) {
            sendToTelegram(chatId, "[-] Los IDs deben ser números.", null);
        } catch (Exception e) {
            sendToTelegram(chatId, "[-] Error al actualizar el estado: " + e.getMessage(), null);
        }
    }

    // ─── Multi-step creation flows ───────────────────────────────────────────────

    private void handleCreationFlow(Long chatId, String text, UserSession session) {
        switch (session.getState()) {

            // SPRINT CREATION
            case CREATING_SPRINT_NOMBRE -> {
                session.setTempValue("sprint_nombre", text.trim());
                session.setState(UserSession.SessionState.CREATING_SPRINT_FECHA_INICIO);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "📋 *Crear Sprint* — Paso 2/4\nFecha de *inicio* (YYYY-MM-DD):", null);
            }
            case CREATING_SPRINT_FECHA_INICIO -> {
                if (!isValidDate(text.trim())) {
                    sendToTelegram(chatId, "[-] Formato inválido. Usa YYYY-MM-DD (ej: 2025-01-15):", null);
                    return;
                }
                session.setTempValue("sprint_fecha_inicio", text.trim());
                session.setState(UserSession.SessionState.CREATING_SPRINT_FECHA_FIN);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "📋 *Crear Sprint* — Paso 3/4\nFecha de *fin* (YYYY-MM-DD) o '-' para omitir:", null);
            }
            case CREATING_SPRINT_FECHA_FIN -> {
                String val = text.trim();
                if (!val.equals("-") && !isValidDate(val)) {
                    sendToTelegram(chatId, "[-] Formato inválido. Usa YYYY-MM-DD o '-' para omitir:", null);
                    return;
                }
                session.setTempValue("sprint_fecha_fin", val.equals("-") ? null : val);
                session.setState(UserSession.SessionState.CREATING_SPRINT_PROYECTO);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "📋 *Crear Sprint* — Paso 4/4\n*ID del proyecto* al que pertenece:", null);
            }
            case CREATING_SPRINT_PROYECTO -> {
                try {
                    Long proyectoId = Long.parseLong(text.trim());
                    CreateSprintRequestDTO dto = new CreateSprintRequestDTO();
                    dto.setNombre(session.getTempValue("sprint_nombre"));
                    dto.setFechaInicio(LocalDate.parse(session.getTempValue("sprint_fecha_inicio")));
                    String fechaFinStr = session.getTempValue("sprint_fecha_fin");
                    dto.setFechaFin(fechaFinStr != null ? LocalDate.parse(fechaFinStr) : null);
                    dto.setIdProyecto(proyectoId);

                    taskClient.createSprint(dto);

                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "✅ Sprint *" + dto.getNombre() + "* creado en el proyecto #" + proyectoId + ".", null);
                } catch (NumberFormatException e) {
                    sendToTelegram(chatId, "[-] El ID del proyecto debe ser un número. Intenta de nuevo:", null);
                } catch (Exception e) {
                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "[-] Error al crear el sprint: " + e.getMessage(), null);
                }
            }

            // TASK CREATION
            case CREATING_TAREA_TITULO -> {
                session.setTempValue("tarea_titulo", text.trim());
                session.setState(UserSession.SessionState.CREATING_TAREA_DESCRIPCION);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 2/8\n*Descripción* (o '-' para omitir):", null);
            }
            case CREATING_TAREA_DESCRIPCION -> {
                String val = text.trim();
                session.setTempValue("tarea_descripcion", val.equals("-") ? null : val);
                session.setState(UserSession.SessionState.CREATING_TAREA_FECHA_LIMITE);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 3/8\n*Fecha límite* (YYYY-MM-DD) o '-' para omitir:", null);
            }
            case CREATING_TAREA_FECHA_LIMITE -> {
                String val = text.trim();
                if (!val.equals("-") && !isValidDate(val)) {
                    sendToTelegram(chatId, "[-] Formato inválido. Usa YYYY-MM-DD o '-' para omitir:", null);
                    return;
                }
                session.setTempValue("tarea_fecha_limite", val.equals("-") ? null : val);
                session.setState(UserSession.SessionState.CREATING_TAREA_TIEMPO_ESTIMADO);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 4/8\n*Tiempo estimado* en horas (número entero):", null);
            }
            case CREATING_TAREA_TIEMPO_ESTIMADO -> {
                try {
                    int horas = Integer.parseInt(text.trim());
                    if (horas < 1) throw new NumberFormatException();
                    session.setTempValue("tarea_tiempo_estimado", String.valueOf(horas));
                    session.setState(UserSession.SessionState.CREATING_TAREA_PROYECTO);
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 5/8\n*ID del proyecto*:", null);
                } catch (NumberFormatException e) {
                    sendToTelegram(chatId, "[-] Introduce un número entero mayor que 0:", null);
                }
            }
            case CREATING_TAREA_PROYECTO -> {
                try {
                    Long.parseLong(text.trim());
                    session.setTempValue("tarea_proyecto", text.trim());
                    session.setState(UserSession.SessionState.CREATING_TAREA_SPRINT);
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 6/8\n*ID del sprint* (o '-' para no asignar a ningún sprint):", null);
                } catch (NumberFormatException e) {
                    sendToTelegram(chatId, "[-] El ID debe ser un número. Intenta de nuevo:", null);
                }
            }
            case CREATING_TAREA_SPRINT -> {
                String val = text.trim();
                if (!val.equals("-")) {
                    try {
                        Long.parseLong(val);
                    } catch (NumberFormatException e) {
                        sendToTelegram(chatId, "[-] Introduce un ID numérico o '-' para omitir:", null);
                        return;
                    }
                }
                session.setTempValue("tarea_sprint", val.equals("-") ? null : val);
                session.setState(UserSession.SessionState.CREATING_TAREA_PRIORIDAD);
                sessionRepository.save(chatId, session);
                sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 7/8\n*Prioridad* (1=Baja, 2=Media, 3=Alta):", null);
            }
            case CREATING_TAREA_PRIORIDAD -> {
                try {
                    int prioridad = Integer.parseInt(text.trim());
                    if (prioridad < 1 || prioridad > 3) throw new NumberFormatException();
                    session.setTempValue("tarea_prioridad", String.valueOf(prioridad));
                    session.setState(UserSession.SessionState.CREATING_TAREA_COMPLEJIDAD);
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 8/8\n*Complejidad* (1-9) o '-' para omitir:", null);
                } catch (NumberFormatException e) {
                    sendToTelegram(chatId, "[-] Introduce 1, 2 o 3 según la prioridad:", null);
                }
            }
            case CREATING_TAREA_COMPLEJIDAD -> {
                String val = text.trim();
                Integer complejidad = null;
                if (!val.equals("-")) {
                    try {
                        complejidad = Integer.parseInt(val);
                        if (complejidad < 1 || complejidad > 9) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        sendToTelegram(chatId, "[-] Introduce un número del 1 al 9 o '-' para omitir:", null);
                        return;
                    }
                }
                try {
                    CreateTaskRequestDTO dto = new CreateTaskRequestDTO();
                    dto.setTitulo(session.getTempValue("tarea_titulo"));
                    dto.setDescripcion(session.getTempValue("tarea_descripcion"));
                    dto.setFechaCreacion(LocalDate.now());
                    String fechaLimiteStr = session.getTempValue("tarea_fecha_limite");
                    dto.setFechaLimite(fechaLimiteStr != null ? LocalDate.parse(fechaLimiteStr) : null);
                    dto.setTiempoEstimado(Integer.parseInt(session.getTempValue("tarea_tiempo_estimado")));
                    dto.setIdProyecto(Long.parseLong(session.getTempValue("tarea_proyecto")));
                    String sprintStr = session.getTempValue("tarea_sprint");
                    dto.setIdSprint(sprintStr != null ? Long.parseLong(sprintStr) : null);
                    dto.setIdEstado(1L);
                    dto.setIdPrioridad(Long.parseLong(session.getTempValue("tarea_prioridad")));
                    dto.setComplejidad(complejidad);

                    TaskDTO creada = taskClient.createTask(dto);

                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId,
                            "[+] Tarea creada exitosamente.\n" +
                            "🆔 *ID:* `" + creada.getIdTarea() + "`\n" +
                            "📌 *Título:* " + creada.getTitulo() + "\n" +
                            "📁 *Proyecto:* #" + creada.getIdProyecto() +
                            (creada.getIdSprint() != null ? " | *Sprint:* #" + creada.getIdSprint() : "") + "\n" +
                            "Usa `/asignar_tarea " + creada.getIdTarea() + " <idUsuario>` para asignarla.",
                            null);
                } catch (Exception e) {
                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "[-] Error al crear la tarea: " + e.getMessage(), null);
                }
            }

            default -> sendToTelegram(chatId, "[!] Estado inesperado. Usa `/cancelar` para reiniciar.", null);
        }
    }

    // ─── KPI commands ────────────────────────────────────────────────────────────

    private void handleKpiCommand(Long chatId, String command, String[] parts, UserSession session) {
        String arg = parts.length > 1 ? parts[1] : null;
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
    }

    private void calcularYEnviarKpi(Long chatId, String command, Long id, String jwtToken) {
        String response;
        String bearer = "Bearer " + jwtToken;
        try {
            switch (command) {
                case "/sprint_duracion" -> {
                    EfficiencyResponseDTO d = kpiClient.getDuracionSprint(id, bearer);
                    response = formatearRespuesta("Duración del Sprint", d.getEfficiencyPercentage(), d.getCalculationDetails(), d.getStatusMessage());
                }
                case "/sprint_cumplimiento" -> {
                    ProductivityResponseDTO c = kpiClient.getCumplimientoSprint(id, bearer);
                    response = formatearRespuesta("Cumplimiento del Sprint", c.getProductivityPercentage(), c.getCalculationDetails(), c.getStatusMessage());
                }
                case "/ciclo_proyecto" -> {
                    ProductivityResponseDTO ci = kpiClient.getTiempoCicloProyecto(id, bearer);
                    response = formatearRespuesta("Tiempo de Ciclo por Tarea", ci.getProductivityPercentage(), ci.getCalculationDetails(), ci.getStatusMessage());
                }
                case "/precision_usuario" -> {
                    EfficiencyResponseDTO p = kpiClient.getPrecisionEstimacionUsuario(id, bearer);
                    response = formatearRespuesta("Precisión de Estimación", p.getEfficiencyPercentage(), p.getCalculationDetails(), p.getStatusMessage());
                }
                default -> response = "[-] Comando desconocido.";
            }
        } catch (Exception e) {
            response = "[-] Error de cálculo. Verifique que el ID exista, el microservicio KPI esté activo y tenga acceso.";
        }
        sendToTelegram(chatId, response, null);
    }

    private void enviarMenu(Long chatId, String command, String jwtToken) {
        String bearer = "Bearer " + jwtToken;
        try {
            List<ActiveResourceDTO> items;
            String tipoRecurso;
            switch (command) {
                case "/sprint_duracion", "/sprint_cumplimiento" -> {
                    items = kpiClient.getSprintsActivos(bearer);
                    tipoRecurso = "el Sprint";
                }
                case "/ciclo_proyecto" -> {
                    items = kpiClient.getProyectosActivos(bearer);
                    tipoRecurso = "el Proyecto";
                }
                case "/precision_usuario" -> {
                    items = kpiClient.getUsuariosActivos(bearer);
                    tipoRecurso = "el Desarrollador";
                }
                default -> {
                    sendToTelegram(chatId, "[!] Comando no reconocido para generar menú.", null);
                    return;
                }
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

    // ─── Help ────────────────────────────────────────────────────────────────────

    private void sendHelp(Long chatId, UserSession session) {
        String helpText =
            "🤖 *Hola " + session.getEmail() + " — Panel de Control*\n\n" +
            "📊 *KPIs Ágiles:*\n" +
            "🔹 `/sprint_duracion [id]`\n" +
            "🔹 `/sprint_cumplimiento [id]`\n" +
            "🔹 `/ciclo_proyecto [id]`\n" +
            "🔹 `/precision_usuario [id]`\n\n" +
            "🗂 *Gestión de Sprints:*\n" +
            "🔹 `/crear_sprint`\n" +
            "🔹 `/sprints <idProyecto>`\n\n" +
            "✅ *Gestión de Tareas:*\n" +
            "🔹 `/crear_tarea`\n" +
            "🔹 `/tareas_sprint <idSprint>`\n" +
            "🔹 `/tareas_usuario <idUsuario>`\n" +
            "🔹 `/asignar_tarea <idTarea> <idUsuario>`\n" +
            "🔹 `/estado_tarea <idTarea> <idEstado>` _(1=TODO, 2=EN PROGRESO, 3=COMPLETADA)_\n\n" +
            "⚙️ `/cancelar` — cancela la operación activa\n\n" +
            "_Si omites el ID en KPIs, te mostraré un menú interactivo._";
        sendToTelegram(chatId, helpText, null);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    private String formatearListaTareas(String contexto, List<TaskDTO> tareas) {
        if (tareas == null || tareas.isEmpty()) {
            return "[-] No hay tareas registradas para " + contexto + ".";
        }
        StringBuilder sb = new StringBuilder("📋 *Tareas de " + contexto + "*\n\n");
        for (TaskDTO t : tareas) {
            sb.append("🔹 `[ID: ").append(t.getIdTarea()).append("]` *").append(t.getTitulo()).append("*\n");
            sb.append("   Estado: ").append(estadoLabel(t.getIdEstado()));
            sb.append(" | Prioridad: ").append(prioridadLabel(t.getIdPrioridad()));
            if (t.getTiempoEstimado() != null) sb.append(" | Est: ").append(t.getTiempoEstimado()).append("h");
            if (t.getFechaLimite() != null) sb.append("\n   Límite: ").append(t.getFechaLimite());
            sb.append("\n");
        }
        return sb.toString();
    }

    private String estadoLabel(Long estadoId) {
        if (estadoId == null) return "?";
        return switch (estadoId.intValue()) {
            case 1 -> "TODO";
            case 2 -> "EN PROGRESO";
            case 3 -> "COMPLETADA";
            default -> "#" + estadoId;
        };
    }

    private String prioridadLabel(Long prioridadId) {
        if (prioridadId == null) return "?";
        return switch (prioridadId.intValue()) {
            case 1 -> "Baja";
            case 2 -> "Media";
            case 3 -> "Alta";
            default -> "#" + prioridadId;
        };
    }

    private boolean isValidDate(String text) {
        try {
            LocalDate.parse(text);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String formatearRespuesta(String metrica, Double porcentaje, String calculationDetails, String statusMessage) {
        return String.format("[+] *Métrica:* %s\n[+] *Resultado:* `%.2f%%`\n[+] *Detalles:* %s\n[+] *Estado:* %s",
                metrica, porcentaje, calculationDetails != null ? calculationDetails : "N/A", statusMessage);
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
