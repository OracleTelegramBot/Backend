package dev.sammy_ulfh.kpi.repository;

import dev.sammy_ulfh.kpi.projection.KpiProjections.CargaTrabajoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.CumplimientoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.EficienciaProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.ProductividadProjection;
import dev.sammy_ulfh.kpi.model.entity.TareaEntity;
import dev.sammy_ulfh.kpi.projection.KpiProjections.CargaTrabajoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.CumplimientoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.EficienciaProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.ProductividadProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaEntity, Long> {
        List<TareaEntity> findByIdProyecto(Long idProyecto);

        @Query(value = "SELECT t.* FROM Tarea t INNER JOIN Usuario_Tarea ut ON t.id_tarea = ut.id_tarea WHERE ut.id_usuario = :idUsuario", nativeQuery = true)
        List<TareaEntity> findByIdUsuario(@Param("idUsuario") Long idUsuario);

        List<TareaEntity> findByIdSprint(Long idSprint);
}