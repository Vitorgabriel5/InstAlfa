package tinterPJ.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "swipes", uniqueConstraints =
    @UniqueConstraint(columnNames = {"usuario_origem_id", "usuario_destino_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Swipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_origem_id", nullable = false)
    private User usuarioOrigem;

    @ManyToOne
    @JoinColumn(name = "usuario_destino_id" )
    private User usuarioDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SwipeType tipo;

    @Column(name = "data_swpe", nullable = false)
    private LocalDate dataSwpe;

    @PrePersist
    protected void onCreate() {
        dataSwpe = LocalDate.now();
    }

}
