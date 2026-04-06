package dev.sammy_ulfh.kpi.controller;

import dev.sammy_ulfh.kpi.model.dto.*;
import dev.sammy_ulfh.kpi.service.KpiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kpis")
public class KpiController {

    private final KpiService kpiService;

    @Autowired
    public KpiController(KpiService kpiService){
        this.kpiService = kpiService;
    }

    // Tiempo de actualizacion de tareas
    @GetMapping("/update-time")
    public ResponseEntity<UpdateTimeDTO> getUpdateTIme(){
        return ResponseEntity.ok(new UpdateTimeDTO("TASK-1", "Login Design", 4.5, "EN PROGRESO")); // Dato temporal
    }

    // Horas promedio por tareas
    @GetMapping("/average-hours")
    public ResponseEntity<AverageHoursDTO> getAverageHours() {
        return ResponseEntity.ok(new AverageHoursDTO("Sprint 1", 15, 6.2));
    }

    // Registro de horas por tarea
    @GetMapping("/time-tracking")
    public ResponseEntity<TimeTrackingDTO> getTimeTracking(){
        return ResponseEntity.ok(new TimeTrackingDTO("Liliana", 32.5, 5));
    }

    // Porcentaje de tareas visibles
    @GetMapping("/visible-tasks")
    public ResponseEntity<VisibleTasksDTO> getVisibleTasks(){
        return ResponseEntity.ok(kpiService.getVisibleTasks());
    }

    // Productividad del equipo (20% estimado o requerido)
    @GetMapping("/productivity")
    public ResponseEntity<ProductivityResponseDTO> getProductivity(){
        return ResponseEntity.ok(new ProductivityResponseDTO(22.5, "Objetivo superado!"));
    }

}
