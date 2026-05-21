package AlfaInsta.demo.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthLoginRequest {
    private String provider;
    private String idToken;
    private String accessToken;
}
