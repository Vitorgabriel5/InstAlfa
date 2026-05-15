package tinterPJ.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.NotificationDTO;
import tinterPJ.demo.model.Notification;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.NotificationRepository;
import tinterPJ.demo.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void create(UUID toUserId, UUID fromUserId, String type, String text) {
        Notification n = new Notification();
        n.setToUserId(toUserId);
        n.setFromUserId(fromUserId);
        n.setType(type);
        n.setText(text);
        n.setRead(false);
        n.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(n);
    }

    public List<NotificationDTO> getNotifications(UUID userId) {
        return notificationRepository
                .findByToUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public long countUnread(UUID userId) {
        return notificationRepository.countByToUserIdAndReadFalse(userId);
    }

    public void markAllAsRead(UUID userId) {
        notificationRepository.findByToUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .filter(n -> !n.isRead())
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });
    }

    private NotificationDTO toDTO(Notification n) {
        User from = userRepository.findById(n.getFromUserId()).orElse(null);
        return NotificationDTO.builder()
                .id(n.getId())
                .fromUserId(n.getFromUserId())
                .fromUsername(from != null ? from.getUsername() : "unknown")
                .fromAvatar(from != null ? from.getProfilePicture() : null)
                .type(n.getType())
                .text(n.getText())
                .read(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}