package dev.sammy_ulfh.kpi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductivityResponseDTO {
    private Double productivityPercentage;
    private String statusMessage;
}
