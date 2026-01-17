package tinterPJ.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "assinaturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "plano_id", nullable = false)
    private SubscriptionPlan plano;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.TRIAL;

    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;

    @Column(name = "data_expiracao")
    private LocalDateTime dataExpiracao;

    @Column(name = "data_cancelamento")
    private LocalDateTime dataCancelamento;

    @Column(name = "renovacao_automatica")
    private Boolean renovacaoAutomatica = false;

    @Column(name = "em_trial")
    private Boolean emTrial = false;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        ultimaAtualizacao = LocalDateTime.now();
        if (dataInicio == null) {
            dataInicio = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaAtualizacao = LocalDateTime.now();
    }

    // Método para verificar se a assinatura está ativa
    public boolean isAtiva() {
        if (status == SubscriptionStatus.CANCELADA || status == SubscriptionStatus.SUSPENSA) {
            return false;
        }

        if (dataExpiracao != null && dataExpiracao.isBefore(LocalDateTime.now())) {
            return false;
        }

        return status == SubscriptionStatus.ATIVA || status == SubscriptionStatus.TRIAL;
    }

    // Método para verificar se expirou
    public boolean isExpirada() {
        return dataExpiracao != null && dataExpiracao.isBefore(LocalDateTime.now());
    }
}
