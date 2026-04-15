package dev.sammy_ulfh.telegram.dto.telegram;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TelegramResponseDTO {
    private Long chat_id;
    private String text;
    private String parse_mode;
}
