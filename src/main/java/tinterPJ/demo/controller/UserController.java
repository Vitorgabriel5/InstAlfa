package tinterPJ.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tinterPJ.demo.dto.UpdateUserRequest;
import tinterPJ.demo.model.User;
import tinterPJ.demo.service.FileStorageService;
import tinterPJ.demo.service.NotificationService;
import tinterPJ.demo.service.UserService;
import tinterPJ.demo.dto.UserResponseDTO;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByUsername(username);

        return ResponseEntity.ok(
                userService.toResponse(user, user.getId())
        );
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody User user) {

        User created = userService.create(user);

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userService.findByUsername(username);

        return ResponseEntity.status(201)
                .body(userService.toResponse(created, currentUser.getId()));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userService.findByUsername(username);

        return ResponseEntity.ok(
                userService.toResponseList(
                        userService.findAll(),
                        currentUser.getId()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User currentUser = userService.findByUsername(username);
        User targetUser = userService.findById(id);

        return ResponseEntity.ok(
                userService.toResponse(targetUser, currentUser.getId())
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateMe(@RequestBody UpdateUserRequest request) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(userService.update(user.getId(), request));
    }

    @PutMapping("/me/username")
    public ResponseEntity<UserResponseDTO> updateUsername(
            @RequestParam String username
    ) {

        String currentUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByUsername(currentUsername);

        user.setUsername(username);

        User updated = userService.save(user);

        return ResponseEntity.ok(
                userService.toResponse(updated, updated.getId())
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable UUID id) {

        userService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted!");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload-profile-picture")
    public ResponseEntity<UserResponseDTO> uploadProfilePicture(
            @RequestParam("file") MultipartFile file
    ) {

        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByUsername(username);

        String fileUrl = fileStorageService.saveFile(file);

        user.setProfilePicture(fileUrl);

        User updated = userService.save(user);

        return ResponseEntity.ok(
                userService.toResponse(updated, user.getId())
        );
    }
}