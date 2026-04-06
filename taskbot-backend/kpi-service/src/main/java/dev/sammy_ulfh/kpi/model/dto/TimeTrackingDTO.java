package dev.sammy_ulfh.kpi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeTrackingDTO {
    private String developerName;
    private Double totalHoursLogged;
    private Integer tasksInvolved;
}
