package tinterPJ.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tinterPJ.demo.dto.ChatMessageDTO;
import tinterPJ.demo.model.User;
import tinterPJ.demo.service.ChatService;
import tinterPJ.demo.service.UserService;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

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
        return ResponseEntity.ok(
                chatService.sendMessage(me.getId(), receiverId, content, type)
        );
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