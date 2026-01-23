package tinterPJ.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario1_id", nullable = false)
    private User usuario1;

    @ManyToOne
    @JoinColumn(name = "usuario2_id", nullable = false)
    private User usuario2;

    @Column(name = "data_match", nullable = false)
    private LocalDateTime dataMatch;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @Column(name = "bloqueado")
    private Boolean bloqueado = false;

    @Column(name = "bloqueado_por_id")
    private Long bloqueadoPorId;

    @Column(name = "data_desfez")
    private LocalDateTime dataDesfez;

    @PrePersist
    protected void onCreate() {
        dataMatch = LocalDateTime.now();
    }


    // Verifica se um usuario faz parte  do match
    public boolean contemUsuario(Long usuarioId) {
        return usuario1.getId().equals(usuarioId) || usuario2.getId().equals(usuarioId);
    }

    // Retorna o outro usuario do match
    public User getOutroUsuario(Long usuarioId) {
        if (usuario1 != null && usuario1.getId() != null && usuario1.getId().equals(usuarioId)) {
            return usuario2;
        }
        if (usuario2 != null && usuario2.getId() != null && usuario2.getId().equals(usuarioId)) {
            return usuario1;
        }
        throw new IllegalArgumentException("Usuario nao faz parte do match");
    }
}
