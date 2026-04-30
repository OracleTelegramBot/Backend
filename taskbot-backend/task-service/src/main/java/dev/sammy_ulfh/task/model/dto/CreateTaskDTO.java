package dev.sammy_ulfh.task.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskDTO {

    @NotBlank
    @Size(max = 100)
    private String titulo;

    @Size(max = 255)
    private String descripcion;

    @NotNull
    private LocalDate fechaCreacion;

    private LocalDate fechaLimite;

    private Integer tiempoEstimado;

    @NotNull
    private Long idProyecto;

    private Long idSprint;

    @NotNull
    private Long idEstado;

    @NotNull
    private Long idPrioridad;

    private Integer complejidad;
}