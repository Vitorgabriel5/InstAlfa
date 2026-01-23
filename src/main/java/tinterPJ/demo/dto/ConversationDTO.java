package tinterPJ.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long matchId;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioFoto;
    private ChatMessageDTO ultimaMensagem;
    private Long mensagemNaoLida;
    private LocalDateTime dataMatch;
}
