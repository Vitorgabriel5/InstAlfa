package AlfaInsta.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import AlfaInsta.demo.dto.ChatMessageDTO;
import AlfaInsta.demo.model.User;
import AlfaInsta.demo.service.ChatService;
import AlfaInsta.demo.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        return userService.findByUsername(username);
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> send(
            @RequestParam UUID receiverId,
            @RequestParam String content,
            @RequestParam(required = false, defaultValue = "text") String type
    ) {
        User me = getCurrentUser();
        ChatMessageDTO msg = chatService.sendMessage(me.getId(), receiverId, content, type);

        User receiver = userService.findById(receiverId);
        messagingTemplate.convertAndSendToUser(
                receiver.getUsername(),
                "/queue/messages",
                msg
        );

        return ResponseEntity.ok(msg);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> unreadCount() {
        User me = getCurrentUser();
        long count = chatService.countUnread(me.getId());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/conversation/{otherId}")
    public ResponseEntity<List<ChatMessageDTO>> getConversation(
            @PathVariable UUID otherId
    ) {
        User me = getCurrentUser();
        chatService.markAsSeen(otherId, me.getId());
        return ResponseEntity.ok(
                chatService.getConversation(me.getId(), otherId)
        );
    }
}