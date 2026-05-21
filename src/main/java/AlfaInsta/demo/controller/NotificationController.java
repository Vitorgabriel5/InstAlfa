package AlfaInsta.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import AlfaInsta.demo.dto.NotificationDTO;
import AlfaInsta.demo.model.User;
import AlfaInsta.demo.service.NotificationService;
import AlfaInsta.demo.service.UserService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userService.findByUsername(username);
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAll() {
        User me = getCurrentUser();
        return ResponseEntity.ok(notificationService.getNotifications(me.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        User me = getCurrentUser();
        return ResponseEntity.ok(
                Map.of("count", notificationService.countUnread(me.getId()))
        );
    }

    @PostMapping("/mark-read")
    public ResponseEntity<Void> markAllRead() {
        User me = getCurrentUser();
        notificationService.markAllAsRead(me.getId());
        return ResponseEntity.ok().build();
    }
}