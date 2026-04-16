package dev.sammy_ulfh.kpi.repository;

import dev.sammy_ulfh.kpi.model.entity.KpiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KpiRepository extends JpaRepository<KpiEntity, Long> {
    
    // Métodos para consultar KPIs históricos
    List<KpiEntity> findByIdProyecto(Long idProyecto);
    List<KpiEntity> findByIdUsuario(Long idUsuario);
}
