package tinterPJ.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinterPJ.demo.model.Match;
import tinterPJ.demo.model.User;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchDTO {
    private Long matchId;
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioFoto;
    private LocalDateTime dataMatch;
    private Boolean ativo;
    private Long destinatarioId;

    public static MatchDTO fromEntity(Match match, User currentUser) {
        User otherUser = match.getOutroUsuario(currentUser.getId());

        return MatchDTO.builder()
                .matchId(match.getId())
                .destinatarioId(currentUser.getId())
                .usuarioId(otherUser.getId())
                .usuarioNome(otherUser.getNome())
                .dataMatch(match.getDataMatch())
                .ativo(match.getAtivo())
                .build();
    }
}
