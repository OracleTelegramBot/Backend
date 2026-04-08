package dev.sammy_ulfh.kpi.service;

import dev.sammy_ulfh.kpi.model.dto.*;

public interface KpiService {
    ProductivityResponseDTO calcularProductividadProyecto(Long idProyecto);
    EfficiencyResponseDTO calcularEficienciaProyecto(Long Id);
    UserWorkloadDTO obtenerCargaTrabajoUsuario(Long idUsuario);
    DeadlineComplianceDTO calcularCumplimientoPlazos(Long idProyecto);
}
