package AlfaInsta.demo.controller;

import AlfaInsta.demo.dto.PostResponseDTO;
import AlfaInsta.demo.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import AlfaInsta.demo.model.User;
import AlfaInsta.demo.service.PostService;
import AlfaInsta.demo.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> create(@RequestParam String content, @RequestParam(required = false) String imageUrl){
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.create(content, imageUrl, user.getId()));
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getFeed(user.getId()));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyPosts() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getPostsByUser(user.getId()));
    }

    @GetMapping("/explore")
    public ResponseEntity<?> getExplorePosts() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getExplorePosts(user.getId()));
    }

    // ✅ NOVO ENDPOINT - Buscar post por ID
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostById(@PathVariable UUID postId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.getPostById(postId, user.getId()));
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> like(@PathVariable UUID postId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        postService.toggleLike(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/repost")
    public ResponseEntity<?> repost(@PathVariable UUID postId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.repost(postId, user.getId()));
    }

    @DeleteMapping("/{postId}/repost")
    public ResponseEntity<?> removeRepost(@PathVariable UUID postId) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        postService.removeRepost(postId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> comment(
            @PathVariable UUID postId,
            @RequestParam String content) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(postService.addComment(postId, user.getId(), content));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.getComments(postId));
    }
}