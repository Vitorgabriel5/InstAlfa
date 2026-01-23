package tinterPJ.demo.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent  implements Serializable {

    private Long messageId;
    private Long matchId;
    private Long remetenteId;
    private String remetenteNome;
    private Long destinatarioId;
    private String conteudo;
    private String tipoMensagem;
    private LocalDateTime dataEnvio;
}
