package dev.sammy_ulfh.task.repository;


import dev.sammy_ulfh.task.model.entity.UsuarioTarea;
import dev.sammy_ulfh.task.model.entity.UsuarioTareaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioTareaRepository extends JpaRepository<UsuarioTarea, UsuarioTareaId> {

    List<UsuarioTarea> findByIdIdUsuario(Long idUsuario);
}