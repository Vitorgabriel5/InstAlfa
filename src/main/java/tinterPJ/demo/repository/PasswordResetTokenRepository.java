package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tinterPJ.demo.model.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByToken(String token);
}
