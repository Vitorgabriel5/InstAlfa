package tinterPJ.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
    @NotBlank(message = "Username é obrigatório")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    private String password;
}
