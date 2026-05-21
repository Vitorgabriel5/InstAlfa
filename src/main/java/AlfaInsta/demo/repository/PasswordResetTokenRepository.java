package AlfaInsta.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import AlfaInsta.demo.model.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);
}
