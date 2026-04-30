package dev.sammy_ulfh.task.repository;


import dev.sammy_ulfh.task.model.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findByIdProyecto(Long idProyecto);
}