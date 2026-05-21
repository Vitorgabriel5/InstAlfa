package AlfaInsta.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import AlfaInsta.demo.dto.ChatMessageDTO;
import AlfaInsta.demo.model.ChatMessage;
import AlfaInsta.demo.repository.ChatMessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessageDTO sendMessage(UUID senderId, UUID receiverId, String content, String type) {
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(senderId);
        msg.setReceiverId(receiverId);
        msg.setContent(content);
        msg.setType(type != null ? type : "text");
        msg.setSeen(false);
        msg.setCreatedAt(LocalDateTime.now());

        ChatMessage saved = chatMessageRepository.save(msg);
        return toDTO(saved);
    }

    public List<ChatMessageDTO> getConversation(UUID userId, UUID otherId) {
        return chatMessageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
                        userId, otherId, userId, otherId
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public void markAsSeen(UUID senderId, UUID receiverId) {
        chatMessageRepository.findByReceiverIdAndSeenFalse(receiverId)
                .stream()
                .filter(m -> m.getSenderId().equals(senderId))
                .forEach(m -> {
                    m.setSeen(true);
                    chatMessageRepository.save(m);
                });
    }

    private ChatMessageDTO toDTO(ChatMessage m) {
        return ChatMessageDTO.builder()
                .id(m.getId())
                .senderId(m.getSenderId())
                .receiverId(m.getReceiverId())
                .content(m.getContent())
                .type(m.getType())
                .seen(m.isSeen())
                .createdAt(m.getCreatedAt())
                .build();
    }

    // ✅ RENOMEAR DE getUnreadCount PARA countUnread
    public long countUnread(UUID receiverId) {
        return chatMessageRepository
                .findByReceiverIdAndSeenFalse(receiverId)
                .size();
    }
}