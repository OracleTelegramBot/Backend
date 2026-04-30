package dev.sammy_ulfh.task.repository;


import dev.sammy_ulfh.task.model.entity.RegistroHoras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroHorasRepository extends JpaRepository<RegistroHoras, Long> {

    List<RegistroHoras> findByIdTarea(Long idTarea);
}