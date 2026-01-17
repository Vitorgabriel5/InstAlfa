package tinterPJ.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSubscriptionRequest {
    @NotNull(message = "ID do plano é obrigatório")
    private Long planoId;

    private Boolean trial = false;
}