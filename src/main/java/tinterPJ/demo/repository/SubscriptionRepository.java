package tinterPJ.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tinterPJ.demo.model.Subscription;
import tinterPJ.demo.model.SubscriptionStatus;
import tinterPJ.demo.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUsuario(User usuario);
    Optional<Subscription> findByUsuarioId(Long usuarioId);
    List<Subscription> findByStatus(SubscriptionStatus status);

    @Query("SELECT s FROM Subscription s WHERE s.dataExpiracao < :now AND s.status = 'ATIVA'")
    List<Subscription> findExpiradas(LocalDateTime now);
}