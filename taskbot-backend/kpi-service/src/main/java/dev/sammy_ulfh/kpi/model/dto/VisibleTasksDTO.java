package dev.sammy_ulfh.kpi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VisibleTasksDTO {
    private Integer totalTasks;
    private Integer visibleTasks;
    private Double visiblePercentage;
}
