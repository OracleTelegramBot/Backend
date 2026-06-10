import re

with open("src/main/java/dev/sammy_ulfh/telegram/service/impl/TelegramServiceImpl.java", "r") as f:
    content = f.read()

# 1. Update handleLoggedInCommand dispatcher
content = re.sub(
    r'case "/sprints" -> handleSprints\(chatId, parts\);',
    r'case "/sprints" -> handleSprints(chatId, parts, session);',
    content
)
content = re.sub(
    r'case "/tareas_sprint" -> handleTareasSprint\(chatId, parts\);',
    r'case "/tareas_sprint" -> handleTareasSprint(chatId, parts, session);',
    content
)
content = re.sub(
    r'case "/tareas_usuario" -> handleTareasUsuario\(chatId, parts\);',
    r'case "/tareas_usuario" -> handleTareasUsuario(chatId, parts, session);',
    content
)
content = re.sub(
    r'case "/asignar_tarea" -> handleAsignarTarea\(chatId, parts, session\);', # if it exists
    r'case "/asignar_tarea" -> handleAsignarTarea(chatId, parts, session);',
    content
)
content = re.sub(
    r'case "/asignar_tarea" -> handleAsignarTarea\(chatId, parts\);',
    r'case "/asignar_tarea" -> handleAsignarTarea(chatId, parts, session);',
    content
)
content = re.sub(
    r'case "/estado_tarea" -> handleEstadoTarea\(chatId, parts\);',
    r'case "/estado_tarea" -> handleEstadoTarea(chatId, parts, session);',
    content
)

# 2. Update handleCallbackQuery
callback_new = """
        int lastIndex = data.lastIndexOf("_");
        if (lastIndex > 0) {
            String command = data.substring(0, lastIndex);
            try {
                Long id = Long.parseLong(data.substring(lastIndex + 1));
                if (command.startsWith("SET_")) {
                    handleCreationFlow(chatId, String.valueOf(id), session);
                } else if (command.equals("CMD_SPRINTS")) {
                    handleSprints(chatId, new String[]{"/sprints", String.valueOf(id)}, session);
                } else if (command.equals("CMD_TSPRINT")) {
                    handleTareasSprint(chatId, new String[]{"/tareas_sprint", String.valueOf(id)}, session);
                } else if (command.equals("CMD_TUSER")) {
                    handleTareasUsuario(chatId, new String[]{"/tareas_usuario", String.valueOf(id)}, session);
                } else {
                    calcularYEnviarKpi(chatId, command, id, session.getJwtToken());
                }
            } catch (NumberFormatException e) {
                sendToTelegram(chatId, "[-] Error procesando el ID desde el botón.", null);
            }
        }
"""
content = re.sub(
    r'int lastIndex = data\.lastIndexOf\("_"\);[\s\S]*?}\s*}\s*}\s*// ─── Login',
    callback_new + "    }\n\n    // ─── Login",
    content
)

# 3. Update handleSprints
content = re.sub(
    r'private void handleSprints\(Long chatId, String\[\] parts\) {\s*if \(parts\.length < 2\) {\s*sendToTelegram\(chatId, "\[-\] Uso: `/sprints <idProyecto>`", null\);\s*return;\s*}',
    r"""private void handleSprints(Long chatId, String[] parts, UserSession session) {
        if (parts.length < 2) {
            enviarMenuGeneral(chatId, "CMD_SPRINTS", session.getJwtToken(), "el Proyecto", kpiClient.getProyectosActivos("Bearer " + session.getJwtToken()));
            return;
        }""",
    content
)

# 4. Update handleTareasSprint
content = re.sub(
    r'private void handleTareasSprint\(Long chatId, String\[\] parts\) {\s*if \(parts\.length < 2\) {\s*sendToTelegram\(chatId, "\[-\] Uso: `/tareas_sprint <idSprint>`", null\);\s*return;\s*}',
    r"""private void handleTareasSprint(Long chatId, String[] parts, UserSession session) {
        if (parts.length < 2) {
            enviarMenuGeneral(chatId, "CMD_TSPRINT", session.getJwtToken(), "el Sprint", kpiClient.getSprintsActivos("Bearer " + session.getJwtToken()));
            return;
        }""",
    content
)

# 5. Update handleTareasUsuario
content = re.sub(
    r'private void handleTareasUsuario\(Long chatId, String\[\] parts\) {\s*if \(parts\.length < 2\) {\s*sendToTelegram\(chatId, "\[-\] Uso: `/tareas_usuario <idUsuario>`", null\);\s*return;\s*}',
    r"""private void handleTareasUsuario(Long chatId, String[] parts, UserSession session) {
        if (parts.length < 2) {
            enviarMenuGeneral(chatId, "CMD_TUSER", session.getJwtToken(), "el Usuario", kpiClient.getUsuariosActivos("Bearer " + session.getJwtToken()));
            return;
        }""",
    content
)

# 6. Update handleAsignarTarea
content = re.sub(
    r'private void handleAsignarTarea\(Long chatId, String\[\] parts\) {\s*if \(parts\.length < 3\) {\s*sendToTelegram\(chatId, "\[-\] Uso: `/asignar_tarea <idTarea> <idUsuario>`", null\);\s*return;\s*}',
    r"""private void handleAsignarTarea(Long chatId, String[] parts, UserSession session) {
        if (parts.length < 3) {
            session.setState(UserSession.SessionState.AWAITING_ASIGNAR_TAREA);
            sessionRepository.save(chatId, session);
            sendToTelegram(chatId, "✅ *Asignar Tarea*\nIntroduce el *ID de la tarea* a asignar:", null);
            return;
        }""",
    content
)

# 7. Update handleEstadoTarea
content = re.sub(
    r'private void handleEstadoTarea\(Long chatId, String\[\] parts\) {\s*if \(parts\.length < 3\) {\s*sendToTelegram\(chatId, "\[-\] Uso: `/estado_tarea <idTarea> <idEstado>`[\\n\s_A-Za-z0-9=,]*", null\);\s*return;\s*}',
    r"""private void handleEstadoTarea(Long chatId, String[] parts, UserSession session) {
        if (parts.length < 3) {
            session.setState(UserSession.SessionState.AWAITING_ESTADO_TAREA);
            sessionRepository.save(chatId, session);
            sendToTelegram(chatId, "✅ *Actualizar Estado*\nIntroduce el *ID de la tarea*:", null);
            return;
        }""",
    content
)

# 8. Update handleCreationFlow - SPRINTS
sprint_proyecto_prompt = """sendToTelegram(chatId, "📋 *Crear Sprint* — Paso 4/4\\n*ID del proyecto* al que pertenece:", null);"""
sprint_proyecto_new = """try {
                    List<ActiveResourceDTO> proyectos = kpiClient.getProyectosActivos("Bearer " + session.getJwtToken());
                    Map<String, Object> replyMarkup = crearTecladoInline(proyectos, "SET_PROYECTO");
                    sendToTelegram(chatId, "📋 *Crear Sprint* — Paso 4/4\\nSelecciona el *proyecto* al que pertenece:", replyMarkup);
                } catch(Exception e) {
                    sendToTelegram(chatId, "📋 *Crear Sprint* — Paso 4/4\\n*ID del proyecto* al que pertenece:", null);
                }"""
content = content.replace(sprint_proyecto_prompt, sprint_proyecto_new)

# 9. Update handleCreationFlow - TAREAS
tarea_proyecto_prompt = """sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 5/8\\n*ID del proyecto*:", null);"""
tarea_proyecto_new = """try {
                        List<ActiveResourceDTO> proyectos = kpiClient.getProyectosActivos("Bearer " + session.getJwtToken());
                        Map<String, Object> replyMarkup = crearTecladoInline(proyectos, "SET_PROYECTO");
                        sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 5/8\\nSelecciona el *proyecto*:", replyMarkup);
                    } catch(Exception ex) {
                        sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 5/8\\n*ID del proyecto*:", null);
                    }"""
content = content.replace(tarea_proyecto_prompt, tarea_proyecto_new)

tarea_sprint_prompt = """sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 6/8\\n*ID del sprint* (o '-' para no asignar a ningún sprint):", null);"""
tarea_sprint_new = """try {
                        List<ActiveResourceDTO> sprints = kpiClient.getSprintsActivos("Bearer " + session.getJwtToken());
                        Map<String, Object> replyMarkup = crearTecladoInline(sprints, "SET_SPRINT");
                        sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 6/8\\nSelecciona el *sprint* (o escribe '-' para omitir):", replyMarkup);
                    } catch(Exception ex) {
                        sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 6/8\\n*ID del sprint* (o '-' para omitir):", null);
                    }"""
content = content.replace(tarea_sprint_prompt, tarea_sprint_new)

tarea_prioridad_prompt = """sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 7/8\\n*Prioridad* (1=Baja, 2=Media, 3=Alta):", null);"""
tarea_prioridad_new = """
                List<ActiveResourceDTO> prioridades = new ArrayList<>();
                prioridades.add(new ActiveResourceDTO(1L, "Baja"));
                prioridades.add(new ActiveResourceDTO(2L, "Media"));
                prioridades.add(new ActiveResourceDTO(3L, "Alta"));
                Map<String, Object> replyMarkup = crearTecladoInline(prioridades, "SET_PRIORIDAD");
                sendToTelegram(chatId, "✅ *Crear Tarea* — Paso 7/8\\nSelecciona la *prioridad*:", replyMarkup);
"""
content = content.replace(tarea_prioridad_prompt, tarea_prioridad_new)


# 10. Add INTERACTIVE FLOWS block to handleCreationFlow switch
interactive_flows = """
            // INTERACTIVE COMMANDS
            case AWAITING_ASIGNAR_TAREA -> {
                try {
                    Long tareaId = Long.parseLong(text.trim());
                    session.setTempValue("asignar_tarea_id", String.valueOf(tareaId));
                    session.setState(UserSession.SessionState.AWAITING_ASIGNAR_USUARIO);
                    sessionRepository.save(chatId, session);
                    
                    try {
                        List<ActiveResourceDTO> usuarios = kpiClient.getUsuariosActivos("Bearer " + session.getJwtToken());
                        Map<String, Object> replyMarkup = crearTecladoInline(usuarios, "SET_USUARIO");
                        sendToTelegram(chatId, "✅ *Asignar Tarea*\\nSelecciona el *usuario* a asignar:", replyMarkup);
                    } catch(Exception ex) {
                        sendToTelegram(chatId, "✅ *Asignar Tarea*\\nIntroduce el *ID del usuario*:", null);
                    }
                } catch(NumberFormatException e) {
                    sendToTelegram(chatId, "[-] Introduce un ID numérico:", null);
                }
            }
            case AWAITING_ASIGNAR_USUARIO -> {
                try {
                    Long userId = Long.parseLong(text.trim());
                    Long tareaId = Long.parseLong(session.getTempValue("asignar_tarea_id"));
                    
                    taskClient.assignTask(tareaId, userId);
                    sendToTelegram(chatId, "[+] Tarea #" + tareaId + " asignada correctamente al usuario #" + userId + ".", null);
                    
                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                } catch(NumberFormatException e) {
                    sendToTelegram(chatId, "[-] Introduce un ID numérico:", null);
                } catch(Exception e) {
                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "[-] Error al asignar la tarea: " + e.getMessage(), null);
                }
            }
            case AWAITING_ESTADO_TAREA -> {
                try {
                    Long tareaId = Long.parseLong(text.trim());
                    session.setTempValue("estado_tarea_id", String.valueOf(tareaId));
                    session.setState(UserSession.SessionState.AWAITING_ESTADO_NUEVO);
                    sessionRepository.save(chatId, session);
                    
                    List<ActiveResourceDTO> estados = new ArrayList<>();
                    estados.add(new ActiveResourceDTO(1L, "TODO"));
                    estados.add(new ActiveResourceDTO(2L, "EN PROGRESO"));
                    estados.add(new ActiveResourceDTO(3L, "COMPLETADA"));
                    Map<String, Object> replyMarkup = crearTecladoInline(estados, "SET_ESTADO");
                    
                    sendToTelegram(chatId, "✅ *Actualizar Estado*\\nSelecciona el *nuevo estado*:", replyMarkup);
                } catch(NumberFormatException e) {
                    sendToTelegram(chatId, "[-] Introduce un ID numérico:", null);
                }
            }
            case AWAITING_ESTADO_NUEVO -> {
                try {
                    Long estadoId = Long.parseLong(text.trim());
                    Long tareaId = Long.parseLong(session.getTempValue("estado_tarea_id"));
                    
                    TaskDTO tarea = taskClient.updateTaskStatus(tareaId, estadoId);
                    sendToTelegram(chatId, "[+] Tarea #" + tarea.getIdTarea() + " *" + tarea.getTitulo() + "* actualizada a *" + estadoLabel(estadoId) + "*.", null);
                    
                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                } catch(NumberFormatException e) {
                    sendToTelegram(chatId, "[-] Introduce un ID numérico:", null);
                } catch(Exception e) {
                    session.setState(UserSession.SessionState.LOGGED_IN);
                    session.clearTempData();
                    sessionRepository.save(chatId, session);
                    sendToTelegram(chatId, "[-] Error al actualizar el estado: " + e.getMessage(), null);
                }
            }
            """
content = re.sub(
    r'default -> sendToTelegram\(chatId, "\[!\] Estado inesperado',
    interactive_flows + r'default -> sendToTelegram(chatId, "[!] Estado inesperado',
    content
)


# 11. Add enviarMenuGeneral helper
helper = """
    private void enviarMenuGeneral(Long chatId, String command, String jwtToken, String tipoRecurso, List<ActiveResourceDTO> items) {
        try {
            if (items == null || items.isEmpty()) {
                sendToTelegram(chatId, "[-] No hay registros activos disponibles en la base de datos.", null);
                return;
            }
            Map<String, Object> replyMarkup = crearTecladoInline(items, command);
            sendToTelegram(chatId, "Selecciona " + tipoRecurso + " para continuar:", replyMarkup);
        } catch (Exception e) {
            sendToTelegram(chatId, "[!] Error al obtener lista de registros.", null);
        }
    }
"""
content = re.sub(
    r'private void sendHelp\(Long chatId, UserSession session\) {',
    helper + "\n    private void sendHelp(Long chatId, UserSession session) {",
    content
)


with open("src/main/java/dev/sammy_ulfh/telegram/service/impl/TelegramServiceImpl.java", "w") as f:
    f.write(content)
