package dev.sammy_ulfh.task.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "kpi-service", url = "${kpi.service.url}")
public interface KpiServiceClient {

    @PostMapping("/api/kpi/recalculate/{idProyecto}")
    void recalcularKpis(@PathVariable("idProyecto") Long idProyecto);
}