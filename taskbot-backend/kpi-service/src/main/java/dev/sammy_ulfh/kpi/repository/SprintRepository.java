package dev.sammy_ulfh.kpi.repository;

import dev.sammy_ulfh.kpi.model.entity.SprintEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SprintRepository extends JpaRepository<SprintEntity, Long> {
}