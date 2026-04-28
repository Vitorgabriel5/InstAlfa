package tinterPJ.demo.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tinterPJ.demo.dto.AuthRequest;
import tinterPJ.demo.dto.AuthResponse;
import tinterPJ.demo.dto.RegisterRequest;
import tinterPJ.demo.model.PasswordResetToken;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.PasswordResetTokenRepository;
import tinterPJ.demo.repository.UserRepository;
import tinterPJ.demo.service.AuthService;
import tinterPJ.demo.service.EmailService;
import tinterPJ.demo.service.UserService;

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
                "Clique aqui: http://localhost:3000/reset?token=" + token
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