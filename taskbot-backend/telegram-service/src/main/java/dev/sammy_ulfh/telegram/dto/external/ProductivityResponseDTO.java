package dev.sammy_ulfh.telegram.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
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
