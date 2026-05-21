package AlfaInsta.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User  {


    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(length = 500)
    private String bio;

    private String profilePicture;
    private String coverPicture;

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String nome;

    @Email(message = "Email inválido")
    @Column(unique = true)
    private String email;

    @Size(min = 3, max = 50)
    @Column(unique = true, length = 255)
    private String username;

    @JsonIgnore
    @Size(min = 8, max = 100)
    @Column(length = 255)
    private String password;

    @Column(name = "oauth_provider")
    private String oauthProvider;

    @Column(name = "oauth_id")
    private String oauthId;

}