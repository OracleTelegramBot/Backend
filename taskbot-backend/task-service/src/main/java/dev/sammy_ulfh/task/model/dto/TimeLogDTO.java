package dev.sammy_ulfh.task.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeLogDTO {

    @NotNull
    private Long idUsuario;

    @NotNull
    @Positive
    private BigDecimal horasTrabajadas;

    @NotNull
    private LocalDate fecha;
}