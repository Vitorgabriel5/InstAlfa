package tinterPJ.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tinterPJ.demo.model.User;
import tinterPJ.demo.service.PostService;
import tinterPJ.demo.service.UserService;

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

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> like(@PathVariable UUID postId) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByUsername(username);

        postService.toggleLike(postId, user.getId());

        return ResponseEntity.ok().build();
    }

}
