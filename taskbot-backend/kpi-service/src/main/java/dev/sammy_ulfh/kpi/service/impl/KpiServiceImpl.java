package dev.sammy_ulfh.kpi.service.impl;

import dev.sammy_ulfh.kpi.projection.KpiProjections.CargaTrabajoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.CumplimientoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.EficienciaProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.ProductividadProjection;
import dev.sammy_ulfh.kpi.exception.ResourceNotFoundException;
import dev.sammy_ulfh.kpi.model.dto.*;
import dev.sammy_ulfh.kpi.model.entity.KpiEntity;
import dev.sammy_ulfh.kpi.model.entity.TareaEntity;
import dev.sammy_ulfh.kpi.repository.KpiRepository;
import dev.sammy_ulfh.kpi.repository.TareaRepository;
import dev.sammy_ulfh.kpi.service.KpiService;
import java.util.Date;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KpiServiceImpl implements KpiService {

    private final TareaRepository tareaRepository;
    private final KpiRepository kpiRepository;

    // Constante para facilitar futuros cambios
    private static final Long ESTADO_COMPLETADO = 5L;

    public KpiServiceImpl(TareaRepository tareaRepository, KpiRepository kpiRepository) {
        this.tareaRepository = tareaRepository;
        this.kpiRepository = kpiRepository;
    }

    @Override
    public ProductivityResponseDTO calcularProductividadProyecto(Long idProyecto) {

        List<TareaEntity> tareas = tareaRepository.findByIdProyecto(idProyecto);

        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException(
                    "El proyecto con ID " + idProyecto + " no existe o no tiene tareas asociadas.");
        }

        double totalTareas = tareas.size();
        // ESTADO COMPLETADO (5L)
        double tareasCompletadas = tareas.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado().equals(ESTADO_COMPLETADO))
                .count();

        double kpiProgreso = (tareasCompletadas / totalTareas) * 100.0;

        guardarRegistroKpi("PRODUCTIVIDAD", kpiProgreso, idProyecto, null);

        ProductivityResponseDTO dto = new ProductivityResponseDTO();
        dto.setProductivityPercentage(kpiProgreso);
        dto.setStatusMessage("Cálculo de productividad exitoso");

        return dto;
    }

    @Override
    public EfficiencyResponseDTO calcularEficienciaProyecto(Long idProyecto) {
        List<TareaEntity> tareas = tareaRepository.findByIdProyecto(idProyecto);

        int totalEstimado = tareas.stream()
                .mapToInt(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0)
                .sum();

        int totalReal = tareas.stream()
                .mapToInt(t -> t.getTiempoReal() != null ? t.getTiempoReal() : 0)
                .sum();

        if (totalEstimado == 0)
            return new EfficiencyResponseDTO(0.0, "No hay tiempos estimados cargados");

        double indice = ((double) totalEstimado / (totalReal == 0 ? 1 : totalReal)) * 100;

        String mensaje = (indice < 100) ? "Retraso detectado" : "Ejecucion eficiente";

        guardarRegistroKpi("EFICIENCIA", indice, idProyecto, null);

        return new EfficiencyResponseDTO(indice, mensaje);
    }

    @Override
    public UserWorkloadDTO obtenerCargaTrabajoUsuario(Long idUsuario) {
        List<TareaEntity> tareas = tareaRepository.findByIdUsuario(idUsuario);

        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException(
                    "El usuario con ID " + idUsuario + " no existe o no tiene tareas asociadas.");
        }

        // ESTADO_COMPLETADO (5L)
        long completadas = tareas.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado().equals(ESTADO_COMPLETADO))
                .count();

        long pendientes = tareas.size() - completadas;
        double porcentaje = ((double) pendientes / tareas.size()) * 100;

        guardarRegistroKpi("CARGA_TRABAJO", porcentaje, null, idUsuario);

        return new UserWorkloadDTO("Usuario" + idUsuario, pendientes, completadas, porcentaje);
    }

    @Override
    public DeadlineComplianceDTO calcularCumplimientoPlazos(Long idProyecto) {

        List<TareaEntity> tareas = tareaRepository.findByIdProyecto(idProyecto);

        List<TareaEntity> tareasConPlazo = tareas.stream()
                .filter(t -> t.getFechaLimite() != null)
                .collect(Collectors.toList());

        if (tareasConPlazo.isEmpty()) {
            throw new ResourceNotFoundException(
                    "El proyecto con ID " + idProyecto + " no existe o no tiene tareas asociadas con plazos.");
        }

        long delayed = tareasConPlazo.stream()
                .filter(t -> {
                    java.util.Date hoy = new java.util.Date();
                    // Una tarea está retrasada si su estado no es ESTADO_COMPLETADO (5L)
                    return (t.getIdEstado() == null || !t.getIdEstado().equals(ESTADO_COMPLETADO))
                            && hoy.after(t.getFechaLimite());
                }).count();

        double porcentaje = ((double) (tareasConPlazo.size() - delayed) / tareasConPlazo.size()) * 100;

        guardarRegistroKpi("CUMPLIMIENTO_PLAZOS", porcentaje, idProyecto, null);

        return new DeadlineComplianceDTO(
                porcentaje,
                (long) tareasConPlazo.size(),
                delayed,
                porcentaje < 80 ? "[!] Alerta: Muchos retrasos" : "[+] Mensaje: Buen ritmo de entrega");
    }

    private void guardarRegistroKpi(String tipo, Double valor, Long idProyecto, Long idUsuario) {
        KpiEntity kpi = new KpiEntity();
        kpi.setTipo(tipo);
        kpi.setValor(valor);
        kpi.setFechaCalculo(new java.util.Date());
        kpi.setIdProyecto(idProyecto);
        kpi.setIdUsuario(idUsuario);

        kpiRepository.save(kpi);
    }
}