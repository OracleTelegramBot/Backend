package dev.sammy_ulfh.task.model.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponseDTO {

    private Long idTarea;
    private String titulo;
    private String descripcion;
    private LocalDate fechaCreacion;
    private LocalDate fechaLimite;
    private Integer tiempoEstimado;
    private Integer tiempoReal;
    private Long idProyecto;
    private Long idSprint;
    private Long idEstado;
    private Long idPrioridad;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer complejidad;
}