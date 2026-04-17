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
import dev.sammy_ulfh.kpi.repository.ProyectoRepository;
import dev.sammy_ulfh.kpi.repository.SprintRepository;
import dev.sammy_ulfh.kpi.repository.TareaRepository;
import dev.sammy_ulfh.kpi.repository.UsuarioRepository;
import dev.sammy_ulfh.kpi.service.KpiService;
import java.util.Date;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KpiServiceImpl implements KpiService {

    private final TareaRepository tareaRepository;
    private final KpiRepository kpiRepository;
    private final ProyectoRepository proyectoRepository;
    private final SprintRepository sprintRepository;
    private final UsuarioRepository usuarioRepository;

    // Constante para facilitar futuros cambios
    private static final Long ESTADO_COMPLETADO = 3L;

    public KpiServiceImpl(TareaRepository tareaRepository, KpiRepository kpiRepository,
            ProyectoRepository proyectoRepository, SprintRepository sprintRepository,
            UsuarioRepository usuarioRepository) {
        this.tareaRepository = tareaRepository;
        this.kpiRepository = kpiRepository;
        this.proyectoRepository = proyectoRepository;
        this.sprintRepository = sprintRepository;
        this.usuarioRepository = usuarioRepository;
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
        guardarRegistroKpi("DURACION_SPRINT", porcentajeDuracion, idProyecto, null, idSprint);

        String mensaje;
        if (porcentajeDuracion <= 90) {
            mensaje = "[+] Excelente resultado: El sprint fue finalizado con un 10% de ahorro o más.";
        } else if (porcentajeDuracion <= 100) {
            mensaje = "[+] Buen resultado: El sprint fue finalizado dentro de lo planeado.";
        } else {
            mensaje = "[!] Estado de alerta: El tiempo real excedió el considerado en la planificación.";
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
        guardarRegistroKpi("CUMPLIMIENTO_SPRINT", porcentajeCumplimiento, idProyecto, null, idSprint);

        String mensaje;
        if (porcentajeCumplimiento >= 85.0) {
            mensaje = "[+] Excelente resultado: Se completó el 85% o más de las tareas planificadas.";
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
                    "El proyecto con ID " + idProyecto + " no tiene tareas completadas para medir el ciclo.");
        }

        double promedioCicloReal = completadas.stream()
                .mapToDouble(t -> t.getTiempoReal() != null ? t.getTiempoReal() : 0.0)
                .average()
                .orElse(0.0);

        double promedioEstimado = completadas.stream()
                .mapToDouble(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0.0)
                .average()
                .orElse(0.0);

        guardarRegistroKpi("TIEMPO_CICLO", promedioCicloReal, idProyecto, null, null);

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
                    + " no tiene tareas con tiempo real registrado para evaluar su precisión.");
        }

        double totalEstimado = tareasEvaluables.stream()
                .mapToDouble(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0.0)
                .sum();

        double totalReal = tareasEvaluables.stream()
                .mapToDouble(t -> t.getTiempoReal() != null ? t.getTiempoReal() : 0.0)
                .sum();

        double precision = (totalEstimado / totalReal) * 100.0;

        guardarRegistroKpi("PRECISION_ESTIMACION", precision, null, idUsuario, null);

        String mensaje;

        if (precision >= 85.0 && precision <= 100.0) {
            mensaje = "[+] Excelente resultado: La estimación de tiempos es altamente precisa.";
        } else if (precision >= 100.0) {
            mensaje = "[-] Desviación: El usuario sobreestima el tiempo de sus tareas (siempre termina antes).";
        } else {
            mensaje = "[!] Alerta: El usuario subestima el tiempo de sus tareas (le lleva más tiempo del planeado).";
        }

        return new EfficiencyResponseDTO(
                Math.round(precision * 100) / 100.0,
                mensaje + " (" + Math.round(precision) + "%)");

    }

    @Override
    public List<KpiEntity> obtenerHistorialKpisProyecto(Long idProyecto) {
        return kpiRepository.findByIdProyectoOrderByFechaCalculoAsc(idProyecto);
    }

    @Override
    public List<ActiveResourceDTO> listarProyectosActivos() {
        return proyectoRepository.findAll().stream()
                .map(p -> new ActiveResourceDTO(p.getIdProyecto(), p.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ActiveResourceDTO> listarSprintsActivos() {
        return sprintRepository.findAll().stream()
                .map(s -> new ActiveResourceDTO(s.getIdSprint(), s.getNombre()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ActiveResourceDTO> listarUsuariosActivos() {
        return usuarioRepository.findAll().stream()
                // Se concatena nombre y apellido para que se vea bien en el botón
                .map(u -> new ActiveResourceDTO(u.getIdUsuario(), u.getNombre() + " " + u.getApellido()))
                .collect(Collectors.toList());
    }

    @Override
    public ProductivityResponseDTO calcularCumplimientoSprintPersonal(Long idUsuario, Long idSprint) {
        List<TareaEntity> tareasUsuario = tareaRepository.findTareasByUsuarioAndSprint(idUsuario, idSprint);
        if (tareasUsuario.isEmpty()) {
            return new ProductivityResponseDTO(0.0, "El usuario no tiene tareas asignadas en este sprint.");
        }

        long completadas = tareasUsuario.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado().equals(ESTADO_COMPLETADO)).count();
        double porcentaje = ((double) completadas / tareasUsuario.size()) * 100;
        double resultadoRedondeado = Math.round(porcentaje * 100.0) / 100.0;

        guardarRegistroKpi("CUMPLIMIENTO_SPRINT", resultadoRedondeado, null, idUsuario, idSprint);

        return new ProductivityResponseDTO(resultadoRedondeado, "Cumplimiento personal calculado.");
    }

    @Override
    public EfficiencyResponseDTO calcularDuracionSprintPersonal(Long idUsuario, Long idSprint) {
        List<TareaEntity> tareasUsuario = tareaRepository.findTareasByUsuarioAndSprint(idUsuario, idSprint);

        double tiempoRealTotal = tareasUsuario.stream()
                .mapToDouble(t -> t.getTiempoReal() != null ? t.getTiempoReal() : 0.0).sum();
        double tiempoEstimadoTotal = tareasUsuario.stream()
                .mapToDouble(t -> t.getTiempoEstimado() != null ? t.getTiempoEstimado() : 0.0).sum();

        if (tiempoEstimadoTotal == 0.0) {
            return new EfficiencyResponseDTO(0.0, "No hay estimaciones para calcular la duración.");
        }

        double porcentaje = (tiempoRealTotal / tiempoEstimadoTotal) * 100;
        double resultado = Math.round(porcentaje * 100.0) / 100.0;

        guardarRegistroKpi("DURACION_SPRINT", resultado, null, idUsuario, idSprint);

        return new EfficiencyResponseDTO(resultado, "Duración personal calculada.");
    }

    @Override
    public ProductivityResponseDTO calcularTiempoCicloPersonal(Long idUsuario, Long idProyecto) {
        List<TareaEntity> tareas = tareaRepository.findTareasByUsuarioAndProyecto(idUsuario, idProyecto);

        // Solo tomamos en cuenta las tareas ya terminadas (estado 3)
        List<TareaEntity> terminadas = tareas.stream()
                .filter(t -> t.getIdEstado() != null && t.getIdEstado().equals(ESTADO_COMPLETADO)
                        && t.getTiempoReal() != null)
                .toList();

        if (terminadas.isEmpty()) {
            return new ProductivityResponseDTO(0.0, "El usuario no ha completado tareas en este proyecto.");
        }

        double promedioCiclo = terminadas.stream().mapToDouble(TareaEntity::getTiempoReal).average().orElse(0.0);
        double resultado = Math.round(promedioCiclo * 100.0) / 100.0;

        guardarRegistroKpi("TIEMPO_CICLO", resultado, idProyecto, idUsuario, null);

        return new ProductivityResponseDTO(resultado, "Tiempo de ciclo personal calculado.");
    }

    @Override
    public List<GraficaResponseDTO> obtenerHistorialPersonal(Long idUsuario, String tipo) {
        return kpiRepository.findByIdUsuarioAndTipoOrderByFechaCalculoAsc(idUsuario, tipo).stream()
                .map(k -> new GraficaResponseDTO(k.getFechaCalculo(), k.getValor()))
                .toList();
    }

    @Override
    public List<GraficaResponseDTO> obtenerHistorialPersonalPorSprint(Long idUsuario, Long idSprint, String tipo) {
        return kpiRepository.findByIdUsuarioAndIdSprintAndTipoOrderByFechaCalculoAsc(idUsuario, idSprint, tipo).stream()
                .map(k -> new GraficaResponseDTO(k.getFechaCalculo(), k.getValor()))
                .toList();
    }

    private void guardarRegistroKpi(String tipo, Double valor, Long idProyecto, Long idUsuario, Long idSprint) {
        KpiEntity kpi = new KpiEntity();
        kpi.setTipo(tipo);
        kpi.setValor(valor);
        kpi.setFechaCalculo(new java.util.Date());
        kpi.setIdProyecto(idProyecto);
        kpi.setIdUsuario(idUsuario);
        kpi.setIdSprint(idSprint);

        kpiRepository.save(kpi);
    }
}