package dev.sammy_ulfh.kpi.repository;

import dev.sammy_ulfh.kpi.model.entity.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaEntity, Long> {
    // Se crea la query automaticamente "SELECT * FROM Tarea WHERE id_proyecto = ?"
    List<TareaEntity> findByIdProyecto(Long idProyecto);

    // Se crea la query automaticamente "SELECT * FROM Tarea WHERE id_usuario = ?"
    List<TareaEntity> findByIdUsuario(Long idUsuario);
}

