package dev.sammy_ulfh.kpi.service.impl;

import dev.sammy_ulfh.kpi.service.KpiService;
import dev.sammy_ulfh.kpi.model.dto.VisibleTasksDTO;
import org.springframework.stereotype.Service;

@Service
public class KpiServiceImpl implements KpiService {
    
    @Override
    public VisibleTasksDTO getVisibleTasks() {
        return new VisibleTasksDTO(0, 0, 0.0);
    }
}
