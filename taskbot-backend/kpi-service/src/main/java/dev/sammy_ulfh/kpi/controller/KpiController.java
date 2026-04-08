package dev.sammy_ulfh.kpi.controller;

import dev.sammy_ulfh.kpi.model.dto.*;
import dev.sammy_ulfh.kpi.service.KpiService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpis")
@Tag(name = "KPI's del proyecto", description = "Endpoints para el calculo de KPI's para recuperar informacion de productividad.")
public class KpiController {

    private final KpiService kpiService;

    @Autowired
    public KpiController(KpiService kpiService){
        this.kpiService = kpiService;
    }

    // Productividad del equipo (20% estimado o requerido)
    @GetMapping("/proyecto/{idProyecto}/productivity")
    public ResponseEntity<ProductivityResponseDTO> getProductivity(@PathVariable Long idProyecto){
        return ResponseEntity.ok(kpiService.calcularProductividadProyecto(idProyecto));
    }

    @GetMapping("/proyecto/{id}/efficiency")
    public ResponseEntity<EfficiencyResponseDTO> getEfficnecy(@PathVariable Long id){
        return ResponseEntity.ok(kpiService.calcularEficienciaProyecto(id));
    }

    @GetMapping("/usuario/{id}/workload")
    public ResponseEntity<UserWorkloadDTO> getWorkload(@PathVariable Long id){
        return ResponseEntity.ok(kpiService.obtenerCargaTrabajoUsuario(id));
    }

    @GetMapping("/proyecto/{id}/deadline-compliance")
    public ResponseEntity<DeadlineComplianceDTO> getDeadlineCompliance(@PathVariable Long id){
        return ResponseEntity.ok(kpiService.calcularCumplimientoPlazos(id));
    }

}
