package tinterPJ.demo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tinterPJ.demo.model.User;
import tinterPJ.demo.service.SubscriptionService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubscriptionFilter extends OncePerRequestFilter {

    private final SubscriptionService subscriptionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // URLs que não requerem assinatura ativa
    private static final List<String> WHITELIST = List.of(
            "/api/auth/",
            "/api/public/",
            "/api/subscription/plans",
            "/api/subscription/create",
            "/api/subscription/status"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Verificar se a URL está na whitelist
        boolean isWhitelisted = WHITELIST.stream().anyMatch(path::startsWith);

        if (isWhitelisted) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
            User user = (User) auth.getPrincipal();

            // Verificar se o usuário tem assinatura ativa
            boolean acessoPermitido = subscriptionService.verificarAcessoPermitido(user.getId());

            if (!acessoPermitido) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("code", "SUBSCRIPTION_REQUIRED");
                errorResponse.put("message", "Assinatura inativa ou expirada. Renove sua assinatura para continuar.");

                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}