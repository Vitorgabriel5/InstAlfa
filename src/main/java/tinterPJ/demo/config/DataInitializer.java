package tinterPJ.demo.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tinterPJ.demo.model.SubscriptionPlan;
import tinterPJ.demo.repository.SubscriptionPlanRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SubscriptionPlanRepository planRepository;

    @Override
    public void run(String... args) {
        // Criar planos se não existirem

        if (planRepository.findByNome("FREE").isEmpty()) {
            SubscriptionPlan free = new SubscriptionPlan();
            free.setNome("FREE");
            free.setDescricao("Plano gratuito - Acesso limitado");
            free.setPreco(BigDecimal.ZERO);
            free.setDuracaoDias(0);
            free.setPermiteAcesso(false);
            free.setPeriodoTrialDias(7);
            free.setLimiteRecursos(5);
            planRepository.save(free);
            System.out.println("✅ Plano FREE criado");
        }

        if (planRepository.findByNome("BASIC").isEmpty()) {
            SubscriptionPlan basic = new SubscriptionPlan();
            basic.setNome("BASIC");
            basic.setDescricao("Plano Básico - Funcionalidades essenciais");
            basic.setPreco(new BigDecimal("29.90"));
            basic.setDuracaoDias(30);
            basic.setPermiteAcesso(true);
            basic.setPeriodoTrialDias(7);
            basic.setLimiteRecursos(50);
            planRepository.save(basic);
            System.out.println("✅ Plano BASIC criado");
        }

        if (planRepository.findByNome("PREMIUM").isEmpty()) {
            SubscriptionPlan premium = new SubscriptionPlan();
            premium.setNome("PREMIUM");
            premium.setDescricao("Plano Premium - Acesso completo");
            premium.setPreco(new BigDecimal("59.90"));
            premium.setDuracaoDias(30);
            premium.setPermiteAcesso(true);
            premium.setPeriodoTrialDias(14);
            premium.setLimiteRecursos(null); // Ilimitado
            planRepository.save(premium);
            System.out.println("✅ Plano PREMIUM criado");
        }

        if (planRepository.findByNome("ANNUAL").isEmpty()) {
            SubscriptionPlan annual = new SubscriptionPlan();
            annual.setNome("ANNUAL");
            annual.setDescricao("Plano Anual - 12 meses com desconto");
            annual.setPreco(new BigDecimal("499.90"));
            annual.setDuracaoDias(365);
            annual.setPermiteAcesso(true);
            annual.setPeriodoTrialDias(30);
            annual.setLimiteRecursos(null);
            planRepository.save(annual);
            System.out.println("✅ Plano ANNUAL criado");
        }
    }
}