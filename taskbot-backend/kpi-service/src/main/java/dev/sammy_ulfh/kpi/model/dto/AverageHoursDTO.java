package dev.sammy_ulfh.kpi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageHoursDTO {
    private String sprintName;
    private Integer totalTaskCompleted;
    private Double averageHoursPerTask;
}
