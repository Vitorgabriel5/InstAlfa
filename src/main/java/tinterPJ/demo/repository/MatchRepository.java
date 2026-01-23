package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tinterPJ.demo.model.Match;
import tinterPJ.demo.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    @Query("SELECT m FROM Match m WHERE (m.usuario1 = :user OR m.usuario2 = :user) AND m.ativo = true")
    List<Match> findMatchesByUsuario(@Param("user") User user);

    @Query("SELECT m FROM Match m WHERE ((m.usuario1 = :user1 AND m.usuario2 = :user2) OR(m.usuario1 = :user2 AND m.usuario2 = :user1)) AND m.ativo =true ")
    Optional<Match> findMatchBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);

    boolean existsByUsuario1AndUsuario2 (User user1, User user2);
}
