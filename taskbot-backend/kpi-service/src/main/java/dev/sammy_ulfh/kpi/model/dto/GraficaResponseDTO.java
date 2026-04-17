package dev.sammy_ulfh.kpi.model.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraficaResponseDTO {
    private Date fecha;
    private Double valor;
}