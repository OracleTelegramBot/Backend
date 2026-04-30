package dev.sammy_ulfh.task.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TAREA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tarea")
    @SequenceGenerator(name = "seq_tarea", sequenceName = "SEQ_TAREA", allocationSize = 1)
    @Column(name = "ID_TAREA")
    private Long idTarea;

    @Column(name = "TITULO", nullable = false, length = 100)
    private String titulo;

    @Column(name = "DESCRIPCION", length = 255)
    private String descripcion;

    @Column(name = "FECHA_CREACION", nullable = false)
    private LocalDate fechaCreacion;

    @Column(name = "FECHA_LIMITE")
    private LocalDate fechaLimite;

    @Column(name = "TIEMPO_ESTIMADO")
    private Integer tiempoEstimado;

    @Column(name = "TIEMPO_REAL")
    private Integer tiempoReal;

    @Column(name = "ID_PROYECTO", nullable = false)
    private Long idProyecto;

    @Column(name = "ID_SPRINT")
    private Long idSprint;

    @Column(name = "ID_ESTADO", nullable = false)
    private Long idEstado;

    @Column(name = "ID_PRIORIDAD", nullable = false)
    private Long idPrioridad;

    @Column(name = "FECHA_INICIO")
    private LocalDateTime fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDateTime fechaFin;

    @Column(name = "COMPLEJIDAD")
    private Integer complejidad;
}