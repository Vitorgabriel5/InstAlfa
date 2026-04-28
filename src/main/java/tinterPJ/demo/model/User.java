package tinterPJ.demo.model;

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

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50)
    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 100)
    @Column(nullable = false , length = 255)
    private String password;
}