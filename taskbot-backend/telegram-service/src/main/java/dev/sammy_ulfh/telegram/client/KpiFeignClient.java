package dev.sammy_ulfh.telegram.client;

import dev.sammy_ulfh.telegram.dto.external.KpiSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kpi-client", url = "${kpi.service.url}")
public interface KpiFeignClient {

    @GetMapping("/proyecto/{id}/productivity")
    KpiSimpleDTO getProductivity(@PathVariable("id") Long id);

    @GetMapping("/proyecto/{id}/efficiency")
    KpiSimpleDTO getEfficiency(@PathVariable("id") Long id);
}
