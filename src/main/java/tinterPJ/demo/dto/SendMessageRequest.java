package tinterPJ.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tinterPJ.demo.model.MessageType;

@Data
public class SendMessageRequest {

    @NotNull
    private Long matchId;

    @NotBlank
    private String conteudo;

    private MessageType tipo = MessageType.TEXT;

    private String urlMidia;
}
