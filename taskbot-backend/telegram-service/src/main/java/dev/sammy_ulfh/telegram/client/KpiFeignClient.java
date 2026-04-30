package dev.sammy_ulfh.telegram.client;

import dev.sammy_ulfh.telegram.dto.external.ActiveResourceDTO;
import dev.sammy_ulfh.telegram.dto.external.EfficiencyResponseDTO;
import dev.sammy_ulfh.telegram.dto.external.ProductivityResponseDTO;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "kpi-service", url = "${feign.client.config.kpi-service.url}")
public interface KpiFeignClient {

    // Endpoints de listado para menus
    @GetMapping("/api/kpis/proyectos/activos")
    List<ActiveResourceDTO> getProyectosActivos(@RequestHeader("Authorization") String token);

    @GetMapping("/api/kpis/sprints/activos")
    List<ActiveResourceDTO> getSprintsActivos(@RequestHeader("Authorization") String token);

    @GetMapping("/api/kpis/usuarios/activos")
    List<ActiveResourceDTO> getUsuariosActivos(@RequestHeader("Authorization") String token);

    // Endpoints de calculo para KPIs
    @GetMapping("/api/kpis/sprint/{idSprint}/duracion")
    EfficiencyResponseDTO getDuracionSprint(@PathVariable("idSprint") Long idSprint, @RequestHeader("Authorization") String token);

    @GetMapping("/api/kpis/sprint/{idSprint}/cumplimiento")
    ProductivityResponseDTO getCumplimientoSprint(@PathVariable("idSprint") Long idSprint, @RequestHeader("Authorization") String token);

    @GetMapping("/api/kpis/proyecto/{idProyecto}/tiempo-ciclo")
    ProductivityResponseDTO getTiempoCicloProyecto(@PathVariable("idProyecto") Long idProyecto, @RequestHeader("Authorization") String token);

    @GetMapping("/api/kpis/usuario/{idUsuario}/precision-estimacion")
    EfficiencyResponseDTO getPrecisionEstimacionUsuario(@PathVariable("idUsuario") Long idUsuario, @RequestHeader("Authorization") String token);
}