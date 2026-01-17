package tinterPJ.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "planos_assinatura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome; // FREE, BASIC, PREMIUM, etc.

    @Column(length = 1000)
    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(name = "duracao_dias", nullable = false)
    private Integer duracaoDias; // 0 = ilimitado, 30 = mensal, 365 = anual

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "permite_acesso")
    private Boolean permiteAcesso = true; // Se false, bloqueia totalmente

    @Column(name = "limite_recursos")
    private Integer limiteRecursos; // Ex: número máximo de projetos, uploads, etc.

    @Column(name = "periodo_trial_dias")
    private Integer periodoTrialDias = 0; // Período de teste grátis
}