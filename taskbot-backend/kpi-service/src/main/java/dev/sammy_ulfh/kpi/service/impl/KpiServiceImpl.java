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

    // Calculo de eficiencia
    @Override
    public EfficiencyResponseDTO calcularDuracionSprint(Long idSprint) {
        List<TareaEntity> tareas = tareaRepository.findByIdSprint(idSprint);

        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException("El sprint con ID " + idSprint + " no tiene tareas asociadas.");
        }

        double tiempoPlanificado = tareas.stream()
                .mapToDouble(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0.0)
                .sum();

        double tiempoReal = tareas.stream()
                .mapToDouble(t -> t.getTiempoReal() != null ? t.getTiempoReal() : 0.0)
                .sum();

        if (tiempoPlanificado == 0.0) {
            return new EfficiencyResponseDTO(0.0, "No hay tiempo planificado (estimado) en este sprint.");
        }

        double porcentajeDuracion = (tiempoReal / tiempoPlanificado) * 100;

        Long idProyecto = tareas.get(0).getIdProyecto();
        guardarRegistroKpi("DURACION_SPRINT", porcentajeDuracion, idProyecto, null);

        String mensaje;
        if (porcentajeDuracion <= 90) {
            mensaje = "[+] Excelente resultado: El sprint fue finalizado con un 10% de ahorro o mas.";
        } else if (porcentajeDuracion <= 100) {
            mensaje = "[+] Buen resultado: El sprint fue finalizado dentro de lo planeado.";
        } else {
            mensaje = "[!] Estado de alerta: El tiempo real excedio el considerado en la planificacion.";
        }

        return new EfficiencyResponseDTO(porcentajeDuracion, mensaje);
    }

    // Cumplimiento del sprint (que % se logro)
    @Override
    public ProductivityResponseDTO calcularCumplimientoSprint(Long idSprint) {
        List<TareaEntity> tareas = tareaRepository.findByIdSprint(idSprint);

        if (tareas.isEmpty()) {
            throw new ResourceNotFoundException("El sprint con ID " + idSprint + " no tiene tareas asociadas.");
        }

        double totalTareasPlanificadas = tareas.size();

        double tareasCompletadas = tareas.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado().equals(ESTADO_COMPLETADO))
                .count();

        double porcentajeCumplimiento = (tareasCompletadas / totalTareasPlanificadas) * 100;

        Long idProyecto = tareas.get(0).getIdProyecto();
        guardarRegistroKpi("CUMPLIMIENTO_SPRINT", porcentajeCumplimiento, idProyecto, null);

        String mensaje;
        if (porcentajeCumplimiento >= 85.0) {
            mensaje = "[+] Excelente resultado: Se completaron el 85% o mas de las tareas planificadas.";
        } else {
            mensaje = "[!] Estado de alerta: Se completaron menos del 85% de las tareas planificadas.";
        }

        ProductivityResponseDTO dto = new ProductivityResponseDTO();
        dto.setProductivityPercentage(porcentajeCumplimiento);
        dto.setStatusMessage(mensaje + "(" + (int) tareasCompletadas + "/" + (int) totalTareasPlanificadas + "tareas)");

        return dto;
    }

    // Tiempo de ciclo por tarea
    @Override
    public ProductivityResponseDTO calcularTiempoCicloProyecto(Long idProyecto) {
        List<TareaEntity> tareas = tareaRepository.findByIdProyecto(idProyecto);

        List<TareaEntity> completadas = tareas.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado().equals(ESTADO_COMPLETADO))
                .collect(Collectors.toList());

        if (completadas.isEmpty()) {
            return new ProductivityResponseDTO(0.0,
                    "l proyecto con ID " + idProyecto + " no tiene tareas completadas para medir el ciclo.");
        }

        double promedioCicloReal = completadas.stream()
                .mapToDouble(t -> t.getTiempoReal() != null ? t.getTiempoReal() : 0.0)
                .average()
                .orElse(0.0);

        double promedioEstimado = completadas.stream()
                .mapToDouble(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0.0)
                .average()
                .orElse(0.0);

        guardarRegistroKpi("TIEMPO_CICLO", promedioCicloReal, idProyecto, null);

        String mensaje;
        double limiteAceptable = promedioEstimado * 1.10;

        if (promedioCicloReal <= limiteAceptable) {
            mensaje = "[+] Aceptable: El tiempo de ciclo promedio está dentro del margen aceptable.";
        } else {
            mensaje = "[!] Alerta: El tiempo de ciclo promedio supera la estimación por más del 10%.";
        }

        ProductivityResponseDTO dto = new ProductivityResponseDTO();
        dto.setProductivityPercentage(Math.round(promedioCicloReal * 100.0) / 100.0);
        dto.setStatusMessage(mensaje + " (Promedio real: " + Math.round(promedioCicloReal * 10.0) / 10.0
                + " hrs vs Estimado: " + Math.round(promedioEstimado * 10.0) / 10.0 + " hrs)");

        return dto;
    }

    @Override
    public EfficiencyResponseDTO calcularPrecisionEstimacionUsuario(Long idUsuario) {
        List<TareaEntity> tareas = tareaRepository.findByIdUsuario(idUsuario);

        List<TareaEntity> tareasEvaluables = tareas.stream()
                .filter(t -> t.getTiempoReal() != null && t.getTiempoReal() > 0)
                .collect(Collectors.toList());

        if (tareasEvaluables.isEmpty()) {
            throw new ResourceNotFoundException("El usuario con ID " + idUsuario
                    + " no tiene tareas con tiempo real registrado para evaluar su precision.");
        }

        double totalEstimado = tareasEvaluables.stream()
                .mapToDouble(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0.0)
                .sum();

        double totalReal = tareasEvaluables.stream()
                .mapToDouble(t -> t.getTiempoReal())
                .sum();

        double precision = (totalEstimado / totalReal) * 100.0;

        guardarRegistroKpi("PRECISION_ESTIMACION", precision, null, idUsuario);

        String mensaje;

        if (precision >= 85.0 && precision <= 100.0) {
            mensaje = "[+] Excelente resultado: La estimacion de tiempos es altamente precisa.";
        } else if (precision >= 100.0) {
            mensaje = "[-] Desviacion: El usuario sobreestima el tiempo de sus tareas (siempre temrina antes).";
        } else {
            mensaje = "[!] Alerta: El usuario subestima el tiempo de sus tareas (le lleva mas tiempo del planeado).";
        }

        return new EfficiencyResponseDTO(
                Math.round(precision * 100) / 100.0,
                mensaje + " (" + Math.round(precision) + "%)");

    }

    @Override
    public List<KpiEntity> obtenerHistorialKpisProyecto(Long idProyecto) {
        // Se usa el repositorio para traer los datos ordenados cronológicamente
        // para que la gráfica en la web se dibuje correctamente de izquierda a derecha
        return kpiRepository.findByIdProyecto(idProyecto);
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