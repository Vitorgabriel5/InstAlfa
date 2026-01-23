package tinterPJ.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import tinterPJ.demo.dto.AuthResponse;
import tinterPJ.demo.dto.RegisterRequest;
import tinterPJ.demo.model.*;
import tinterPJ.demo.repository.RoleRepository;
import tinterPJ.demo.repository.SubscriptionPlanRepository;
import tinterPJ.demo.repository.UserRepository;
import tinterPJ.demo.security.JwtUtil;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private SubscriptionPlanRepository planRepository;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private Role userRole;
    private SubscriptionPlan planoFree;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setNome("Joao Silva");
        registerRequest.setEmail("joao@gmail.com");
        registerRequest.setUsername("joao");
        registerRequest.setPassword("senha123");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setNome("ROLE_USER");

        planoFree = new SubscriptionPlan();
        planoFree.setId(1L);
        planoFree.setNome("FREE");
        planoFree.setDescricao("Plano gratuito");
        planoFree.setPreco(BigDecimal.ZERO);
        planoFree.setDuracaoDias(0);
        planoFree.setPermiteAcesso(false);
        planoFree.setPeriodoTrialDias(7);

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("Joao Silva");
        savedUser.setEmail("joao@gmail.com");
        savedUser.setPassword("senha123");
        savedUser.setUsername("joao");

    }

    @Test
    void testRegisterPessoaFisica_Success() {

        registerRequest.setTipoUsuario(UserType.PESSOA_FISICA);
        registerRequest.setCpf("12345678900");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findByNome("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any())).thenReturn(savedUser);
        when(planRepository.findByNome("FREE")).thenReturn(Optional.of(planoFree));
        when(subscriptionService.criarAssinatura(anyLong(),anyLong(),anyBoolean())).thenReturn(new Subscription());
        when(jwtUtil.generateToken(any())).thenReturn("token123");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull (response);
        assertNotNull (response.getToken());
        assertEquals("token123", response.getToken());
        assertEquals("joao", response.getUsername());
        verify(userRepository, times(1)).save(any());
        verify(subscriptionService, times(1)).criarAssinatura(anyLong(), anyLong(), eq(true));
    }

    @Test
    void testRegisterPessoaFisica_SemCpf_ThowsException(){

        registerRequest.setTipoUsuario(UserType.PESSOA_FISICA);
        registerRequest.setCpf(null);

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
    }

    @Test
    void testRegisterPessoaJuridica_Success(){

        registerRequest.setTipoUsuario(UserType.PESSOA_JURIDICA);
        registerRequest.setCnpj("12345678000190");
        registerRequest.setRazaoSocial("Tech Company LTDA");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(roleRepository.findByNome("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any())).thenReturn(savedUser);
        when(planRepository.findByNome("FREE")).thenReturn(Optional.of(planoFree));
        when(subscriptionService.criarAssinatura(anyLong(),anyLong(),anyBoolean())).thenReturn(new Subscription());
        when(jwtUtil.generateToken(any())).thenReturn("token123");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull (response);
        assertNotNull (response.getToken());
        verify(userRepository, times(1)).save(any(User.class));
        verify(subscriptionService, times(1)).criarAssinatura(anyLong(),anyLong(), eq(true));
    }

    @Test
    void testRegisterPessoaJuridica_semCnpj_ThowsException(){

        registerRequest.setTipoUsuario(UserType.PESSOA_JURIDICA);
        registerRequest.setCnpj(null);
        registerRequest.setRazaoSocial("Tech Company");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
    }

    @Test
    void testRegister_UsernameJaExiste_ThowsException(){

        registerRequest.setTipoUsuario(UserType.PESSOA_FISICA);
        registerRequest.setCpf("12345678900");

        when(userRepository.existsByUsername("joao")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });

        assertEquals("Username já está em uso",exception.getMessage());
    }

    @Test
    void testRegister_EmailJaExiste_ThowsExeption(){

        registerRequest.setTipoUsuario(UserType.PESSOA_JURIDICA);
        registerRequest.setCpf("1234568900");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail("joao@gmail.com")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerRequest);
        });
        assertEquals("Email já está em uso",exception.getMessage());
    }
}