package dev.sammy_ulfh.kpi.controller;

import dev.sammy_ulfh.kpi.model.dto.*;
import dev.sammy_ulfh.kpi.model.entity.KpiEntity;
import dev.sammy_ulfh.kpi.service.KpiService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpis")
@Tag(name = "KPI's del proyecto", description = "Endpoints para el cálculo de KPI's ágiles (Scrum) y recuperación de histórico.")
public class KpiController {

    private final KpiService kpiService;

    @Autowired
    public KpiController(KpiService kpiService) {
        this.kpiService = kpiService;
    }

    // Duración del sprint (KPI 2)
    @GetMapping("/sprint/{idSprint}/duracion")
    public ResponseEntity<EfficiencyResponseDTO> getDuracionSprint(@PathVariable Long idSprint) {
        return ResponseEntity.ok(kpiService.calcularDuracionSprint(idSprint));
    }

    // Cumplimiento de sprint (KPI 6)
    @GetMapping("/sprint/{idSprint}/cumplimiento")
    public ResponseEntity<ProductivityResponseDTO> getCumplimientoSprint(@PathVariable Long idSprint) {
        return ResponseEntity.ok(kpiService.calcularCumplimientoSprint(idSprint));
    }

    // Tiempo de ciclo por tarea (KPI 7)
    @GetMapping("/proyecto/{idProyecto}/tiempo-ciclo")
    public ResponseEntity<ProductivityResponseDTO> getTiempoCicloProyecto(@PathVariable Long idProyecto) {
        return ResponseEntity.ok(kpiService.calcularTiempoCicloProyecto(idProyecto));
    }

    // Precisión de estimación de carga (KPI 12)
    @GetMapping("/usuario/{idUsuario}/precision-estimacion")
    public ResponseEntity<EfficiencyResponseDTO> getPrecisionEstimacionUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(kpiService.calcularPrecisionEstimacionUsuario(idUsuario));
    }

    // Histórico de KPIs para gráficas en Dashboard
    @GetMapping("/proyecto/{idProyecto}/history")
    public ResponseEntity<List<KpiEntity>> getKpiHistory(@PathVariable Long idProyecto) {
        return ResponseEntity.ok(kpiService.obtenerHistorialKpisProyecto(idProyecto));
    }

    @GetMapping("/proyectos/activos")
    public ResponseEntity<List<ActiveResourceDTO>> getProyectosActivos() {
        return ResponseEntity.ok(kpiService.listarProyectosActivos());
    }

    @GetMapping("/sprints/activos")
    public ResponseEntity<List<ActiveResourceDTO>> getSprintsActivos() {
        return ResponseEntity.ok(kpiService.listarSprintsActivos());
    }

    @GetMapping("/usuarios/activos")
    public ResponseEntity<List<ActiveResourceDTO>> getUsuariosActivos() {
        return ResponseEntity.ok(kpiService.listarUsuariosActivos());
    }
}