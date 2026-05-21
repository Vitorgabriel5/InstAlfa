package AlfaInsta.demo.controller;

import AlfaInsta.demo.dto.DashboardCountsResponse;
import AlfaInsta.demo.model.User;
import AlfaInsta.demo.service.ChatService;
import AlfaInsta.demo.service.NotificationService;
import AlfaInsta.demo.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final NotificationService notificationService;
    private final ChatService chatService;
    private final UserService userService;

    @GetMapping("/counts")
    public DashboardCountsResponse getCounts(Authentication authentication) {

        String username = authentication.getName();
        User user = userService.findByUsername(username);

        long unreadNotifications = notificationService.getUnreadCount(username);

        long unreadMessages = chatService.countUnread(user.getId());

        return new DashboardCountsResponse(
                unreadNotifications,
                unreadMessages
        );
    }
}