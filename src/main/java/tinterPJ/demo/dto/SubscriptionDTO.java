package tinterPJ.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tinterPJ.demo.model.Subscription;
import tinterPJ.demo.model.SubscriptionStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {
    private Long id;
    private String nomePlano;
    private String descricaoPlano;
    private SubscriptionStatus status;
    private LocalDateTime dataInicio;
    private LocalDateTime dataExpiracao;
    private Boolean emTrial;
    private Boolean ativa;
    private Integer diasRestantes;

    public static SubscriptionDTO fromEntity(Subscription subscription) {
        Integer diasRestantes = null;
        if (subscription.getDataExpiracao() != null) {
            long dias = java.time.Duration.between(
                    LocalDateTime.now(),
                    subscription.getDataExpiracao()
            ).toDays();
            diasRestantes = (int) Math.max(0, dias);
        }

        return SubscriptionDTO.builder()
                .id(subscription.getId())
                .nomePlano(subscription.getPlano().getNome())
                .descricaoPlano(subscription.getPlano().getDescricao())
                .status(subscription.getStatus())
                .dataInicio(subscription.getDataInicio())
                .dataExpiracao(subscription.getDataExpiracao())
                .emTrial(subscription.getEmTrial())
                .ativa(subscription.isAtiva())
                .diasRestantes(diasRestantes)
                .build();
    }
}