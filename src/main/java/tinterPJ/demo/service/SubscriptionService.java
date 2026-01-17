package tinterPJ.demo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tinterPJ.demo.dto.SubscriptionDTO;
import tinterPJ.demo.exception.SubscriptionException;
import tinterPJ.demo.model.Subscription;
import tinterPJ.demo.model.SubscriptionPlan;
import tinterPJ.demo.model.SubscriptionStatus;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.SubscriptionPlanRepository;
import tinterPJ.demo.repository.SubscriptionRepository;
import tinterPJ.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPlanRepository planRepository;
    private final UserRepository userRepository;

    @Transactional
    public Subscription criarAssinatura(Long usuarioId, Long planoId, boolean trial) {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        SubscriptionPlan plano = planRepository.findById(planoId)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        // Verificar se já tem assinatura
        if (subscriptionRepository.findByUsuario(usuario).isPresent()) {
            throw new SubscriptionException("Usuário já possui uma assinatura");
        }

        Subscription assinatura = new Subscription();
        assinatura.setUsuario(usuario);
        assinatura.setPlano(plano);
        assinatura.setDataInicio(LocalDateTime.now());

        if (trial && plano.getPeriodoTrialDias() > 0) {
            assinatura.setEmTrial(true);
            assinatura.setStatus(SubscriptionStatus.TRIAL);
            assinatura.setDataExpiracao(LocalDateTime.now().plusDays(plano.getPeriodoTrialDias()));
        } else {
            assinatura.setStatus(SubscriptionStatus.ATIVA);
            if (plano.getDuracaoDias() > 0) {
                assinatura.setDataExpiracao(LocalDateTime.now().plusDays(plano.getDuracaoDias()));
            }
        }

        return subscriptionRepository.save(assinatura);
    }

    @Transactional
    public Subscription atualizarPlano(Long usuarioId, Long novoPlanoId) {
        Subscription assinatura = subscriptionRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new SubscriptionException("Assinatura não encontrada"));

        SubscriptionPlan novoPlano = planRepository.findById(novoPlanoId)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado"));

        assinatura.setPlano(novoPlano);
        assinatura.setEmTrial(false);
        assinatura.setStatus(SubscriptionStatus.ATIVA);
        assinatura.setDataInicio(LocalDateTime.now());

        if (novoPlano.getDuracaoDias() > 0) {
            assinatura.setDataExpiracao(LocalDateTime.now().plusDays(novoPlano.getDuracaoDias()));
        } else {
            assinatura.setDataExpiracao(null);
        }

        return subscriptionRepository.save(assinatura);
    }

    @Transactional
    public Subscription renovarAssinatura(Long usuarioId) {
        Subscription assinatura = subscriptionRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new SubscriptionException("Assinatura não encontrada"));

        SubscriptionPlan plano = assinatura.getPlano();

        assinatura.setEmTrial(false);
        assinatura.setStatus(SubscriptionStatus.ATIVA);
        assinatura.setDataInicio(LocalDateTime.now());

        if (plano.getDuracaoDias() > 0) {
            assinatura.setDataExpiracao(LocalDateTime.now().plusDays(plano.getDuracaoDias()));
        }

        return subscriptionRepository.save(assinatura);
    }

    @Transactional
    public void cancelarAssinatura(Long usuarioId) {
        Subscription assinatura = subscriptionRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new SubscriptionException("Assinatura não encontrada"));

        assinatura.setStatus(SubscriptionStatus.CANCELADA);
        assinatura.setDataCancelamento(LocalDateTime.now());
        assinatura.setRenovacaoAutomatica(false);

        subscriptionRepository.save(assinatura);
    }

    @Transactional
    public void suspenderAssinatura(Long usuarioId) {
        Subscription assinatura = subscriptionRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new SubscriptionException("Assinatura não encontrada"));

        assinatura.setStatus(SubscriptionStatus.SUSPENSA);
        subscriptionRepository.save(assinatura);
    }

    public boolean verificarAcessoPermitido(Long usuarioId) {
        Subscription assinatura = subscriptionRepository.findByUsuarioId(usuarioId)
                .orElse(null);

        if (assinatura == null) {
            return false; // Sem assinatura = sem acesso
        }

        return assinatura.isAtiva();
    }

    public SubscriptionDTO obterAssinatura(Long usuarioId) {
        Subscription assinatura = subscriptionRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new SubscriptionException("Assinatura não encontrada"));

        return SubscriptionDTO.fromEntity(assinatura);
    }

    // Tarefa agendada para verificar assinaturas expiradas (roda todo dia às 2h)
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void verificarAssinaturasExpiradas() {
        List<Subscription> expiradas = subscriptionRepository.findExpiradas(LocalDateTime.now());

        for (Subscription assinatura : expiradas) {
            if (assinatura.getRenovacaoAutomatica()) {
                // Aqui você implementaria a lógica de renovação automática/cobrança
                System.out.println("Tentando renovar assinatura do usuário: " +
                        assinatura.getUsuario().getUsername());
            } else {
                assinatura.setStatus(SubscriptionStatus.EXPIRADA);
                subscriptionRepository.save(assinatura);
            }
        }
    }
}