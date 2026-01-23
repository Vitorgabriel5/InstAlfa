package tinterPJ.demo.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tinterPJ.demo.model.ChatMessage;
import tinterPJ.demo.model.Match;
import tinterPJ.demo.model.User;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByMatchOrderByDataEnvioAsc(Match match);
    Page<ChatMessage> findByMatchOrderByDataEnvioDesc(Match match, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.destinatario = :user AND m.lida =false")
    Long countUnreadMessages(@Param("user")User user);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.match = :match AND m.destinatario = :user AND m.lida = false")
    Long countUnreadMessgesByMatch(@Param("match") Match match, @Param("user") User user);
}
