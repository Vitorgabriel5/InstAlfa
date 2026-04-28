package tinterPJ.demo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.*;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.UserRepository;
import tinterPJ.demo.security.JwtUtil;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username já está em uso");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        User user = new User();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .nome(user.getNome())
                .message("Usuário registrado com sucesso")
                .build();
    }

    public AuthResponse login(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(user.getUsername());

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .nome(user.getNome())
                .message("Login realizado com sucesso")
                .build();
    }


}