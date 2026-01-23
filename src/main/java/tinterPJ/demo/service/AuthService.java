package tinterPJ.demo.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tinterPJ.demo.dto.AuthRequest;
import tinterPJ.demo.dto.AuthResponse;
import tinterPJ.demo.dto.RegisterRequest;
import tinterPJ.demo.model.Role;
import tinterPJ.demo.model.SubscriptionPlan;
import tinterPJ.demo.model.User;
import tinterPJ.demo.model.UserType;
import tinterPJ.demo.repository.RoleRepository;
import tinterPJ.demo.repository.SubscriptionPlanRepository;
import tinterPJ.demo.repository.UserRepository;
import tinterPJ.demo.security.JwtUtil;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanRepository planRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username já está em uso");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já está em uso");
        }

        if (request.getTipoUsuario() == UserType.PESSOA_FISICA) {
            if (request.getCpf() == null || request.getCpf().isEmpty()) {
                throw new RuntimeException("CPF e obrigatorio para Pessoa Fisica");
            } else if(request.getTipoUsuario() == UserType.PESSOA_JURIDICA) {
                if (request.getCnpj() == null || request.getCnpj().isEmpty()) {
                    throw new RuntimeException("CNPJ e obrigatorio para Pessoa Juridica");
                }
                if (request.getRazaoSocial() == null || request.getRazaoSocial().isEmpty()) {
                    throw new RuntimeException("Razao Social e obrigatorio para Pessoa Juridica");
                }
            }
        }

        User user = new User();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAtivo(true);
        user.setTipoUsuario(request.getTipoUsuario());

        user.setCpf(request.getCpf());
        user.setCnpj(request.getCnpj());
        user.setRazaoSocial(request.getRazaoSocial());
        user.setNomeFantasia(request.getNomeFantasia());

        // Atribuir role padrão USER
        Role userRole = roleRepository.findByNome("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role("ROLE_USER");
                    return roleRepository.save(newRole);
                });

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        user = userRepository.save(user);

        // Criar assinatura FREE para novo usuário
        SubscriptionPlan planoFree = planRepository.findByNome("FREE")
                .orElseGet(() -> {
                    SubscriptionPlan newPlan = new SubscriptionPlan();
                    newPlan.setNome("FREE");
                    newPlan.setDescricao("Plano gratuito com funcionalidades limitadas");
                    newPlan.setPreco(new java.math.BigDecimal("0.00"));
                    newPlan.setDuracaoDias(0);
                    newPlan.setPermiteAcesso(false);
                    newPlan.setPeriodoTrialDias(7);
                    return planRepository.save(newPlan);
                });

        subscriptionService.criarAssinatura(user.getId(), planoFree.getId(), true);

        String token = jwtUtil.generateToken(user);

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
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String token = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .nome(user.getNome())
                .message("Login realizado com sucesso")
                .build();
    }

    public AuthResponse refreshToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token inválido");
        }

        String token = authHeader.substring(7);
        String username = jwtUtil.extractUsername(token);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        String newToken = jwtUtil.generateToken(user);

        return AuthResponse.builder()
                .token(newToken)
                .username(user.getUsername())
                .email(user.getEmail())
                .nome(user.getNome())
                .message("Token renovado com sucesso")
                .build();
    }
}