package AlfaInsta.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import AlfaInsta.demo.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameIgnoreCase(String username);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByOauthIdAndOauthProvider(String oauthId, String oauthProvider);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}