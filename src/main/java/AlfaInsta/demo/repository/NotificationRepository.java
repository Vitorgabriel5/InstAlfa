package AlfaInsta.demo.repository;

import AlfaInsta.demo.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {


    long countByToUserIdAndReadFalse(UUID toUserId);
    List<Notification> findByToUserIdOrderByCreatedAtDesc(UUID toUserId);
}