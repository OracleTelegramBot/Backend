package dev.sammy_ulfh.telegram.dto.telegram;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TelegramResponseDTO {
    private Long chat_id;
    private String text;
    private String parse_mode;
    private Object reply_markup;

    public TelegramResponseDTO(Long chat_id, String text, String parse_mode) {
        this.chat_id = chat_id;
        this.text = text;
        this.parse_mode = parse_mode;
        this.reply_markup = null;
    }
}
