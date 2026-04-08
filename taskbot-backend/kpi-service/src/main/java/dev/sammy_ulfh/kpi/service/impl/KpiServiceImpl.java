package dev.sammy_ulfh.kpi.service.impl;

import dev.sammy_ulfh.kpi.exception.ResourceNotFoundException;
import dev.sammy_ulfh.kpi.model.dto.*;
import dev.sammy_ulfh.kpi.model.entity.TareaEntity;
import dev.sammy_ulfh.kpi.repository.TareaRepository;
import dev.sammy_ulfh.kpi.service.KpiService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KpiServiceImpl implements KpiService {

    private final TareaRepository tareaRepository;

    public KpiServiceImpl(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    @Override
    public ProductivityResponseDTO calcularProductividadProyecto(Long idProyecto) {

        // Se solicitan los datos
        List<TareaEntity> tareas = tareaRepository.findByIdProyecto(idProyecto);

        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException("El proyecto con ID " + idProyecto + " no existe o no tiene tareas asociadas.");
        }

        // Progreso del proyecto (KPI 1)
        double totalTareas = tareas.size();
        // Se considera que el ID de estado completado es 4.
        double tareasCompletadas = tareas.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado() == 4L)
                .count();

        double kpiProgreso = (tareasCompletadas / totalTareas) * 100.0;

        ProductivityResponseDTO dto = new ProductivityResponseDTO();
        dto.setProductivityPercentage(kpiProgreso);
        dto.setStatusMessage("Cálculo de productividad exitoso");

        return dto;
    }

    @Override
    public EfficiencyResponseDTO calcularEficienciaProyecto(Long idProyecto){
        List<TareaEntity> tareas = tareaRepository.findByIdProyecto(idProyecto);

        int totalEstimado = tareas.stream()
                .mapToInt(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0)
                .sum();

        int totalReal = tareas.stream()
                .mapToInt(t -> t.getTiempoReal() != null ? t.getTiempoReal() : 0)
                .sum();

        if (totalEstimado == 0) return new EfficiencyResponseDTO(0.0, "No hay tiempos estimados cargados");

        double indice = ((double) totalEstimado / (totalReal == 0 ? 1 : totalReal)) * 100;

        String mensaje = (indice < 100) ? "Retraso detectado" : "Ejecucion eficiente";

        return new EfficiencyResponseDTO(indice, mensaje);
    }

    @Override
    public UserWorkloadDTO obtenerCargaTrabajoUsuario(Long idUsuario){
        List<TareaEntity> tareas = tareaRepository.findByIdUsuario(idUsuario);

        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException("El usuario con ID " + idUsuario + " no existe o no tiene tareas asociadas.");
        }

        long completadas = tareas.stream().filter(t -> t.getIdEstado() != null && t.getIdEstado() == 4L).count();
        long pendientes = tareas.size() - completadas;
        double porcentaje = ((double) pendientes / tareas.size()) * 100;

        return new UserWorkloadDTO("Usuario" + idUsuario, pendientes, completadas, porcentaje);
    }

    @Override
    public DeadlineComplianceDTO calcularCumplimientoPlazos(Long idProyecto){
        List<TareaEntity> tareas = tareaRepository.findByIdProyecto(idProyecto);

        List<TareaEntity> tareasConPlazo = tareas.stream()
                .filter(t -> t.getFechaLimite() != null)
                .collect(Collectors.toList());

        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException("El proyecto con ID " + idProyecto + " no existe o no tiene tareas asociadas.");
        }

        long delayed = tareasConPlazo.stream()
                .filter(t -> {
                    java.util.Date hoy = new java.util.Date();
                    return (t.getIdEstado() == null || t.getIdEstado() != 4L) && hoy.after(t.getFechaLimite());
                }).count();

        double porcentaje = ((double) (tareasConPlazo.size() - delayed) / tareasConPlazo.size()) * 100;

        return new DeadlineComplianceDTO(
                porcentaje,
                (long) tareasConPlazo.size(),
                delayed,
                porcentaje < 80 ? "[!] Alerta: Muchos retrasos" : "[+] Mensaje: Buen ritmo de entrega"
        );
    }
}
