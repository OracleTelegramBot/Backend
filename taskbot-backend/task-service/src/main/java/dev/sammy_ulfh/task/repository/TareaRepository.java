package dev.sammy_ulfh.task.repository;

import dev.sammy_ulfh.task.model.entity.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByIdSprint(Long idSprint);
}