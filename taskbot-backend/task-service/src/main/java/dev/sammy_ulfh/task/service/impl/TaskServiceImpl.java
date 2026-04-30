package dev.sammy_ulfh.task.service.impl;

import dev.sammy_ulfh.task.client.KpiServiceClient;
import dev.sammy_ulfh.task.model.dto.CreateSprintDTO;
import dev.sammy_ulfh.task.model.dto.CreateTaskDTO;
import dev.sammy_ulfh.task.model.dto.TaskResponseDTO;
import dev.sammy_ulfh.task.model.dto.TimeLogDTO;
import dev.sammy_ulfh.task.model.entity.*;
import dev.sammy_ulfh.task.repository.RegistroHorasRepository;
import dev.sammy_ulfh.task.repository.SprintRepository;
import dev.sammy_ulfh.task.repository.TareaRepository;
import dev.sammy_ulfh.task.repository.UsuarioTareaRepository;
import dev.sammy_ulfh.task.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {

    private static final Long ESTADO_COMPLETADA = 3L;

    private final TareaRepository tareaRepository;
    private final SprintRepository sprintRepository;
    private final UsuarioTareaRepository usuarioTareaRepository;
    private final RegistroHorasRepository registroHorasRepository;
    private final KpiServiceClient kpiServiceClient;

    // Consultas

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksByUser(Long userId) {
        log.info("Obteniendo tareas para usuario id={}", userId);
        return usuarioTareaRepository.findByIdIdUsuario(userId)
                .stream()
                .map(ut -> tareaRepository.findById(ut.getId().getIdTarea())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Tarea no encontrada id=" + ut.getId().getIdTarea())))
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDTO> getTasksBySprint(Long sprintId) {
        log.info("Obteniendo tareas para sprint id={}", sprintId);
        return tareaRepository.findByIdSprint(sprintId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sprint> getSprintsByProject(Long projectId) {
        log.info("Obteniendo sprints para proyecto id={}", projectId);
        return sprintRepository.findByIdProyecto(projectId);
    }

    // Tareas
    @Override
    @Transactional
    public TaskResponseDTO createTask(CreateTaskDTO dto) {
        log.info("Creando tarea: {}", dto.getTitulo());
        validarStatusId(dto.getIdEstado());

        Tarea tarea = new Tarea();
        tarea.setTitulo(dto.getTitulo());
        tarea.setDescripcion(dto.getDescripcion());
        tarea.setFechaCreacion(dto.getFechaCreacion() != null ? dto.getFechaCreacion() : LocalDate.now());
        tarea.setFechaLimite(dto.getFechaLimite());
        tarea.setTiempoEstimado(dto.getTiempoEstimado());
        tarea.setTiempoReal(0);
        tarea.setIdProyecto(dto.getIdProyecto());
        tarea.setIdSprint(dto.getIdSprint());
        tarea.setIdEstado(dto.getIdEstado());
        tarea.setIdPrioridad(dto.getIdPrioridad());
        tarea.setComplejidad(dto.getComplejidad());

        Tarea saved = tareaRepository.save(tarea);
        log.info("Tarea creada con id={}", saved.getIdTarea());
        return toResponseDTO(saved);
    }

    @Override
    @Transactional
    public TaskResponseDTO updateTaskStatus(Long taskId, Long statusId) {
        log.info("Actualizando estado de tarea id={} a statusId={}", taskId, statusId);
        validarStatusId(statusId);

        Tarea tarea = tareaRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada id=" + taskId));

        tarea.setIdEstado(statusId);

        if (ESTADO_COMPLETADA.equals(statusId)) {
            tarea.setFechaFin(LocalDateTime.now());
            log.info("Tarea id={} marcada como completada, fechaFin={}", taskId, tarea.getFechaFin());
            tareaRepository.save(tarea);
            triggerKpiRecalculate(tarea.getIdProyecto());
        } else {
            tareaRepository.save(tarea);
        }

        return toResponseDTO(tarea);
    }

    @Override
    @Transactional
    public void assignTask(Long taskId, Long userId) {
        log.info("Asignando tarea id={} a usuario id={}", taskId, userId);

        tareaRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada id=" + taskId));

        UsuarioTareaId id = new UsuarioTareaId(userId, taskId);

        if (usuarioTareaRepository.existsById(id)) {
            log.warn("La tarea id={} ya está asignada al usuario id={}, se omite.", taskId, userId);
            return;
        }

        usuarioTareaRepository.save(new UsuarioTarea(id));
        log.info("Tarea id={} asignada correctamente al usuario id={}", taskId, userId);
    }

    // Horas

    @Override
    @Transactional
    public void logTime(Long taskId, TimeLogDTO dto) {
        log.info("Registrando {} horas en tarea id={} para usuario id={}", dto.getHorasTrabajadas(), taskId, dto.getIdUsuario());

        Tarea tarea = tareaRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada id=" + taskId));

        // Insertar registro en bitácora
        RegistroHoras registro = new RegistroHoras();
        registro.setFecha(dto.getFecha() != null ? dto.getFecha() : LocalDate.now());
        registro.setHorasTrabajadas(dto.getHorasTrabajadas());
        registro.setIdUsuario(dto.getIdUsuario());
        registro.setIdTarea(taskId);
        registroHorasRepository.save(registro);

        // Sumar horas redondeadas al acumulado TIEMPO_REAL
        int horasRedondeadas = dto.getHorasTrabajadas()
                .setScale(0, RoundingMode.CEILING)
                .intValue();

        int tiempoRealActual = tarea.getTiempoReal() != null ? tarea.getTiempoReal() : 0;
        tarea.setTiempoReal(tiempoRealActual + horasRedondeadas);
        tareaRepository.save(tarea);

        log.info("Tarea id={} tiempoReal actualizado a={}", taskId, tarea.getTiempoReal());
    }

    // Sprints

    @Override
    @Transactional
    public void createSprint(CreateSprintDTO dto) {
        log.info("Creando sprint: {}", dto.getNombre());

        Sprint sprint = new Sprint();
        sprint.setNombre(dto.getNombre());
        sprint.setFechaInicio(dto.getFechaInicio());
        sprint.setFechaFin(dto.getFechaFin());
        sprint.setIdProyecto(dto.getIdProyecto());

        Sprint saved = sprintRepository.save(sprint);
        log.info("Sprint creado con id={}", saved.getIdSprint());
    }

    // Privado

    private void validarStatusId(Long statusId) {
        if (statusId == null || statusId < 1 || statusId > 3) {
            throw new IllegalArgumentException("statusId inválido: " + statusId + ". Valores permitidos: 1, 2, 3");
        }
    }

    private void triggerKpiRecalculate(Long idProyecto) {
        try {
            kpiServiceClient.recalcularKpis(idProyecto);
            log.info("KPI recalculado correctamente para proyecto id={}", idProyecto);
        } catch (Exception e) {
            log.error("Error al llamar kpi-service para proyecto id={}: {}", idProyecto, e.getMessage());
        }
    }

    private TaskResponseDTO toResponseDTO(Tarea tarea) {
        return TaskResponseDTO.builder()
                .idTarea(tarea.getIdTarea())
                .titulo(tarea.getTitulo())
                .descripcion(tarea.getDescripcion())
                .fechaCreacion(tarea.getFechaCreacion())
                .fechaLimite(tarea.getFechaLimite())
                .tiempoEstimado(tarea.getTiempoEstimado())
                .tiempoReal(tarea.getTiempoReal())
                .idProyecto(tarea.getIdProyecto())
                .idSprint(tarea.getIdSprint())
                .idEstado(tarea.getIdEstado())
                .idPrioridad(tarea.getIdPrioridad())
                .fechaInicio(tarea.getFechaInicio())
                .fechaFin(tarea.getFechaFin())
                .complejidad(tarea.getComplejidad())
                .build();
    }
}