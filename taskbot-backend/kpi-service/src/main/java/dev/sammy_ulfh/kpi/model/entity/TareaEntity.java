package dev.sammy_ulfh.kpi.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "TAREA")
@Data
public class TareaEntity {

    @Id
    @Column(name = "ID_TAREA")
    private Long idTarea;

    @Column(name = "ID_PROYECTO")
    private Long idProyecto;

    @Column(name = "ID_ESTADO")
    private Long idEstado;

    @Column(name = "TIEMPO_ESTIMADO")
    private Integer tiempoEstimado;

    @Column(name = "TIEMPO_REAL")
    private Integer tiempoReal;

    @Column(name = "FECHA_LIMITE")
    @Temporal(TemporalType.DATE)
    private java.util.Date fechaLimite;
}
