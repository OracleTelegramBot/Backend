package dev.sammy_ulfh.telegram.dto.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveResourceDTO {
    private Long id;
    private String nombre;
}