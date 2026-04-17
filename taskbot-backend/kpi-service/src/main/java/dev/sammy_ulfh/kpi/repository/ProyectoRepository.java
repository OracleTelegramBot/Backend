package dev.sammy_ulfh.kpi.repository;

import dev.sammy_ulfh.kpi.model.entity.ProyectoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoRepository extends JpaRepository<ProyectoEntity, Long> {
}