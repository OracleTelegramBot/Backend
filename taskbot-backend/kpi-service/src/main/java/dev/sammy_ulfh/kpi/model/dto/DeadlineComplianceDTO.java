package dev.sammy_ulfh.kpi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeadlineComplianceDTO {
    private Double compliancePercentage;
    private Long totalTasks;
    private Long delayedTasks;
    private String statusMessage;
}
