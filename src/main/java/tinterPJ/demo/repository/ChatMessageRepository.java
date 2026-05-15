package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tinterPJ.demo.model.ChatMessage;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
            UUID s1, UUID r1, UUID s2, UUID r2
    );

    List<ChatMessage> findByReceiverIdAndSeenFalse(UUID receiverId);
}