package dev.sammy_ulfh.kpi.repository;

import dev.sammy_ulfh.kpi.model.entity.KpiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KpiRepository extends JpaRepository<KpiEntity, Long> {

    // Metodos de consulta para KPI
    List<KpiEntity> findByIdProyectoOrderByFechaCalculoAsc(Long idProyecto);

    List<KpiEntity> findByIdUsuario(Long idUsuario);

    // Historial de un usuario para un KPI sin importar el sprint
    List<KpiEntity> findByIdUsuarioAndTipoOrderByFechaCalculoAsc(Long idUsuario, String tipo);

    // Historial de un usuario para un KPI dentro de un sprint específico
    List<KpiEntity> findByIdUsuarioAndIdSprintAndTipoOrderByFechaCalculoAsc(Long idUsuario, Long idSprint, String tipo);
}
