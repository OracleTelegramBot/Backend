package dev.sammy_ulfh.kpi.service;

import java.util.List;

import dev.sammy_ulfh.kpi.model.dto.*;
import dev.sammy_ulfh.kpi.model.entity.KpiEntity;

public interface KpiService {
    EfficiencyResponseDTO calcularDuracionSprint(Long idSprint);

    ProductivityResponseDTO calcularCumplimientoSprint(Long idSprint);

    ProductivityResponseDTO calcularTiempoCicloProyecto(Long idProyecto);

    EfficiencyResponseDTO calcularPrecisionEstimacionUsuario(Long idUsuario);

    List<KpiEntity> obtenerHistorialKpisProyecto(Long idProyecto);

    List<ActiveResourceDTO> listarProyectosActivos();

    List<ActiveResourceDTO> listarSprintsActivos();

    List<ActiveResourceDTO> listarUsuariosActivos();
}