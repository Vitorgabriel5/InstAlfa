package tinterPJ.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tinterPJ.demo.model.Subscription;
import tinterPJ.demo.model.SubscriptionPlan;
import tinterPJ.demo.model.SubscriptionStatus;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.SubscriptionPlanRepository;
import tinterPJ.demo.repository.SubscriptionRepository;
import tinterPJ.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User user;
    private SubscriptionPlan plan;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setNome("Joao");

        plan = new SubscriptionPlan();
        plan.setId(1L);
        plan.setNome("BASIC");
        plan.setDuracaoDias(30);
        plan.setPeriodoTrialDias(7);
    }

    @Test
    void testCriarAssinatura_ComTrial_Sucesso(){

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findByUsuario(user)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Subscription result = subscriptionService.criarAssinatura(1L,1L, true);

        assertNotNull(result);
        assertTrue(result.getEmTrial());
        assertEquals(SubscriptionStatus.TRIAL, result.getStatus());
        assertNotNull(result.getDataExpiracao());
        verify(subscriptionRepository,times(1)).save(any());
    }

    @Test
    void testCriarAssinatura_SemTrial_Sucesso(){

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findByUsuario(user)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Subscription result = subscriptionService.criarAssinatura(1L,1L, false);

        assertNotNull(result);
        assertFalse(result.getEmTrial());
        assertEquals(SubscriptionStatus.ATIVA, result.getStatus());
    }

    @Test
    void testCriarAssinatura_JaPossuiAssinatura_ThowsException(){

        Subscription existente = new Subscription();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(planRepository.findById(1L)).thenReturn(Optional.of(plan));
        when(subscriptionRepository.findByUsuario(user)).thenReturn(Optional.of(existente));

        assertThrows(RuntimeException.class,()-> {
                subscriptionService.criarAssinatura(1L,1L, false);
        });
    }

    @Test
    void testVerificarAcessoPermitido_AssinaturaAtiva_ReturnsTrue(){

        Subscription subscription = new Subscription();

        subscription.setStatus(SubscriptionStatus.ATIVA);
        subscription.setDataExpiracao(LocalDateTime.now().plusDays(10));

        when(subscriptionRepository.findByUsuarioId(1L)).thenReturn(Optional.of(subscription));

        boolean result = subscriptionService.verificarAcessoPermitido(1L);

        assertTrue(result);
    }

    @Test
    void testVerificarAcessoPermitido_AssinaturaExpirada_ReturnsFalse(){

        Subscription subscription = new Subscription();
        subscription.setStatus(SubscriptionStatus.ATIVA);
        subscription.setDataExpiracao(LocalDateTime.now().minusDays(1));

        when(subscriptionRepository.findByUsuarioId(1L)).thenReturn(Optional.of(subscription));

        boolean result = subscriptionService.verificarAcessoPermitido(1L);

        assertFalse(result);
    }

    @Test
    void testVerificarAcessoPermitido_SemAssinatura_ReturnsFalse(){

        when(subscriptionRepository.findByUsuarioId(1L)).thenReturn(Optional.empty());

        boolean result = subscriptionService.verificarAcessoPermitido(1L);

        assertFalse(result);
    }
}