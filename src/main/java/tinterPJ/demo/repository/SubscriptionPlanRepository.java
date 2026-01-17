package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tinterPJ.demo.model.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    Optional<SubscriptionPlan> findByNome(String nome);
    List<SubscriptionPlan> findByAtivo(Boolean ativo);
}