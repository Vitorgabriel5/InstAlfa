package AlfaInsta.demo.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import AlfaInsta.demo.dto.AuthRequest;
import AlfaInsta.demo.dto.AuthResponse;
import AlfaInsta.demo.dto.RegisterRequest;
import AlfaInsta.demo.model.PasswordResetToken;
import AlfaInsta.demo.model.User;
import AlfaInsta.demo.repository.PasswordResetTokenRepository;
import AlfaInsta.demo.repository.UserRepository;
import AlfaInsta.demo.service.AuthService;
import AlfaInsta.demo.service.EmailService;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUserId(user.getId());
        resetToken.setExpiration(LocalDateTime.now().plusMinutes(15));

        tokenRepository.save(resetToken);

        emailService.sendEmail(
                user.getEmail(),
                "Redefinição de senha",
                "Clique aqui: http://localhost:5173/reset?token=" + token
        );

        return ResponseEntity.ok("Email enviado");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.getExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expirado");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Senha atualizada");
    }
}