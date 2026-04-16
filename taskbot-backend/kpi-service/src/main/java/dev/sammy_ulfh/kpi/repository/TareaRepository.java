package dev.sammy_ulfh.kpi.repository;

import dev.sammy_ulfh.kpi.model.entity.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<TareaEntity, Long> {
    // Se crea la query automaticamente "SELECT * FROM Tarea WHERE id_proyecto = ?"
    List<TareaEntity> findByIdProyecto(Long idProyecto);

    // Consulta nativa que hace JOIN con la tabla intermedia para obtener las tareas de un usuario
    @Query(value = "SELECT t.* FROM Tarea t INNER JOIN Usuario_Tarea ut ON t.id_tarea = ut.id_tarea WHERE ut.id_usuario = :idUsuario", nativeQuery = true)
    List<TareaEntity> findByIdUsuario(@Param("idUsuario") Long idUsuario);
}

