package tinterPJ.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinterPJ.demo.model.ChatMessage;
import tinterPJ.demo.model.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long matchId;
    private Long remetenteId;
    private String remetenteName;
    private Long destinatarioId;
    private String DestinatarioName;
    private String conteudo;
    private MessageType tipoMensagem;
    private String urlMidia;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataLeitura;
    private Boolean lida;

    public static ChatMessageDTO fromEntity(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .matchId(message.getMatch().getId())
                .remetenteId(message.getRemetente().getId())
                .remetenteName(message.getRemetente().getNome())
                .destinatarioId(message.getDestinatario().getId())
                .DestinatarioName(message.getDestinatario().getNome())
                .conteudo(message.getConteudo())
                .tipoMensagem(message.getTipoMensagem())
                .urlMidia(message.getUrlMidia())
                .dataEnvio(message.getDataEnvio())
                .dataLeitura(message.getDataLeitura())
                .lida(message.getLida())
                .build();
    }
}
