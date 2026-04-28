package tinterPJ.demo.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.crypto.password.PasswordEncoder;
import tinterPJ.demo.dto.AuthRequest;
import tinterPJ.demo.dto.AuthResponse;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.UserRepository;
import tinterPJ.demo.security.JwtUtil;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;



    @Test
    void shouldLoginSuccessfully() {

        String username = "testuser";
        String password = "123456";

        User user = new User();
        user.setUsername(username);
        user.setEmail("test@email.com");
        user.setNome("Teste");

        when(authenticationManager.authenticate(any()))
                .thenReturn(null);

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(username))
                .thenReturn("fake-jwt");

        AuthRequest request = new AuthRequest(username, password);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("fake-jwt", response.getToken());
        assertEquals(username, response.getUsername());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername(username);
        verify(jwtUtil).generateToken(username);
    }


    @Test
    void shouldFailWhenAuthenticationFails() {

        String username = "testuser";
        String wrongPassword = "wrong";

        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Bad credentials"));

        AuthRequest request = new AuthRequest(username, wrongPassword);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Bad credentials", exception.getMessage());

        verify(authenticationManager).authenticate(any());
    }
    @Test
    void shouldFailWhenUserNotFound() {

        String username = "noone";

        AuthRequest request = new AuthRequest(username, "123");

        when(authenticationManager.authenticate(any()))
                .thenReturn(null);


        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());

        verify(authenticationManager).authenticate(any());
        verify(userRepository).findByUsername(username);
    }

}
