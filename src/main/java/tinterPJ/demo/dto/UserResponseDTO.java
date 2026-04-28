package tinterPJ.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private UUID id;
    private String nome;
    private String username;
    private String bio;
    private String profilePicture;

    private long followers;
    private long following;
    private boolean isFollowing;

}
