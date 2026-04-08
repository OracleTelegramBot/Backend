package dev.sammy_ulfh.kpi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWorkloadDTO {
    private String nombreUsuario;
    private Long tareasPendientes;
    private Long tareasCompletadas;
    private Double porcentajeCarga;
}
