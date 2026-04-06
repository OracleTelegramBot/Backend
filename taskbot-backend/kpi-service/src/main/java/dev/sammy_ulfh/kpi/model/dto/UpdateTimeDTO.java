package dev.sammy_ulfh.kpi.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTimeDTO {
    private String taskId;
    private String taskName;
    private Double hoursSinceLastUpdate;
    private String status;
}
