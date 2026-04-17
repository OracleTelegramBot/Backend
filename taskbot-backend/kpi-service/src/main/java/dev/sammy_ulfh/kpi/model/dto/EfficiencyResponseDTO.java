package dev.sammy_ulfh.kpi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EfficiencyResponseDTO {
    private Double efficiencyPercentage;
    private String statusMessage;
    private String calculationDetails;
    private Double actualValue;
    private Double expectedValue;
}
