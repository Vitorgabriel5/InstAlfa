package tinterPJ.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tinterPJ.demo.model.SwipeType;

@Data
public class SwipeRequest {

    @NotNull
    private Long usuarioDestinoId;

    @NotNull
    private SwipeType tipo;
}
