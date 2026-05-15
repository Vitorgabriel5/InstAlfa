package tinterPJ.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tinterPJ.demo.model.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByToUserIdOrderByCreatedAtDesc(UUID toUserId);

    long countByToUserIdAndReadFalse(UUID toUserId);
}