package AlfaInsta.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    private String nome;
    private String email;
    private String username;
    private String password;
    private String bio;
    private String profilePicture;

}