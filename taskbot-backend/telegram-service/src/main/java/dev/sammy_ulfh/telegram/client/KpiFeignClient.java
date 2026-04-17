package dev.sammy_ulfh.telegram.client;

import dev.sammy_ulfh.telegram.dto.external.ActiveResourceDTO;
import dev.sammy_ulfh.telegram.dto.external.EfficiencyResponseDTO;
import dev.sammy_ulfh.telegram.dto.external.ProductivityResponseDTO;
import dev.sammy_ulfh.telegram.dto.external.KpiSimpleDTO;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kpi-service", url = "${feign.client.config.kpi-service.url}")
public interface KpiFeignClient {

    // Endpoints de listado para menus
    @GetMapping("/api/kpis/proyectos/activos")
    List<ActiveResourceDTO> getProyectosActivos();

    @GetMapping("/api/kpis/sprints/activos")
    List<ActiveResourceDTO> getSprintsActivos();

    @GetMapping("/api/kpis/usuarios/activos")
    List<ActiveResourceDTO> getUsuariosActivos();

    // Endpoints de calculo para KPIs
    @GetMapping("/api/kpis/sprint/{idSprint}/duracion")
    EfficiencyResponseDTO getDuracionSprint(@PathVariable("idSprint") Long idSprint);

    @GetMapping("/api/kpis/sprint/{idSprint}/cumplimiento")
    ProductivityResponseDTO getCumplimientoSprint(@PathVariable("idSprint") Long idSprint);

    @GetMapping("/api/kpis/proyecto/{idProyecto}/tiempo-ciclo")
    ProductivityResponseDTO getTiempoCicloProyecto(@PathVariable("idProyecto") Long idProyecto);

    @GetMapping("/api/kpis/usuario/{idUsuario}/precision-estimacion")
    EfficiencyResponseDTO getPrecisionEstimacionUsuario(@PathVariable("idUsuario") Long idUsuario);
}