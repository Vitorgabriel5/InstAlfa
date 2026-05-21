package AlfaInsta.demo.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import AlfaInsta.demo.dto.UserResponseDTO;
import AlfaInsta.demo.model.User;
import AlfaInsta.demo.service.FollowService;
import AlfaInsta.demo.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;
    private final UserService userService;

    private UUID getCurrentUserId() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByUsername(username);
        return user.getId();
    }

    @PostMapping("/{userId}")
    public ResponseEntity<String> follow(@PathVariable UUID userId) {
        followService.follow(getCurrentUserId(), userId);
        return ResponseEntity.ok("Seguiu com sucesso");
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> unfollow(@PathVariable UUID userId) {
        followService.unfollow(getCurrentUserId(), userId);
        return ResponseEntity.ok("Deixou de seguir");
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Long>> getStats(@PathVariable UUID userId) {

        Map<String, Long> response = new HashMap<>();
        response.put("followers", followService.countFollowers(userId));
        response.put("following", followService.countFollowing(userId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserResponseDTO>> followers(@PathVariable UUID userId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userService.findByUsername(username);

        return ResponseEntity.ok(
                followService.getFollowers(userId, currentUser.getId())
        );
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserResponseDTO>> following(@PathVariable UUID userId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userService.findByUsername(username);

        return ResponseEntity.ok(
                followService.getFollowing(userId, currentUser.getId())
        );
    }
}