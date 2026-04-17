package dev.sammy_ulfh.kpi.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "SPRINT")
@Data
public class SprintEntity {
    @Id
    @Column(name = "ID_SPRINT")
    private Long idSprint;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "ID_PROYECTO")
    private Long idProyecto;
}
