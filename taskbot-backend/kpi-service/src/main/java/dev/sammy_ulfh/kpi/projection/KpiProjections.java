/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.sammy_ulfh.kpi.projection;

import dev.sammy_ulfh.kpi.projection.KpiProjections.CargaTrabajoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.CumplimientoProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.EficienciaProjection;
import dev.sammy_ulfh.kpi.projection.KpiProjections.ProductividadProjection;
import dev.sammy_ulfh.kpi.model.entity.TareaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author sammy
 */
public interface KpiProjections {

    interface ProductividadProjection {
        Integer getTotalTareas();
        Integer getTareasCompletadas();
        Double getProgresoPorcentaje();
    }

    interface EficienciaProjection {
        Integer getTotalEstimado();
        Integer getTotalReal();
        Double getEficienciaPorcentaje();
    }

    interface CargaTrabajoProjection {
        Integer getTotalAsignadas();
        Integer getCompletadas();
        Integer getPendientes();
    }

    interface CumplimientoProjection {
        Integer getTotalConPlazo();
        Integer getTareasRetrasadas();
    }
}