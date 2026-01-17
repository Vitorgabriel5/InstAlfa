package tinterPJ.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tinterPJ.demo.dto.CreateSubscriptionRequest;
import tinterPJ.demo.dto.SubscriptionDTO;
import tinterPJ.demo.model.Subscription;
import tinterPJ.demo.model.SubscriptionPlan;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.SubscriptionPlanRepository;
import tinterPJ.demo.service.SubscriptionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SubscriptionPlanRepository planRepository;

    @GetMapping("/plans")
    public ResponseEntity<List<SubscriptionPlan>> listarPlanos() {
        return ResponseEntity.ok(planRepository.findByAtivo(true));
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> criarAssinatura(
            @Valid @RequestBody CreateSubscriptionRequest request) {

        User user = getAuthenticatedUser();

        Subscription assinatura = subscriptionService.criarAssinatura(
                user.getId(),
                request.getPlanoId(),
                request.getTrial()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Assinatura criada com sucesso");
        response.put("assinatura", SubscriptionDTO.fromEntity(assinatura));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<SubscriptionDTO> obterStatus() {
        User user = getAuthenticatedUser();
        SubscriptionDTO dto = subscriptionService.obterAssinatura(user.getId());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/upgrade/{planoId}")
    public ResponseEntity<Map<String, Object>> atualizarPlano(@PathVariable Long planoId) {
        User user = getAuthenticatedUser();

        Subscription assinatura = subscriptionService.atualizarPlano(user.getId(), planoId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Plano atualizado com sucesso");
        response.put("assinatura", SubscriptionDTO.fromEntity(assinatura));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/renew")
    public ResponseEntity<Map<String, Object>> renovarAssinatura() {
        User user = getAuthenticatedUser();

        Subscription assinatura = subscriptionService.renovarAssinatura(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Assinatura renovada com sucesso");
        response.put("assinatura", SubscriptionDTO.fromEntity(assinatura));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancelarAssinatura() {
        User user = getAuthenticatedUser();

        subscriptionService.cancelarAssinatura(user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Assinatura cancelada com sucesso");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/suspend/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> suspenderAssinatura(@PathVariable Long userId) {
        subscriptionService.suspenderAssinatura(userId);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Assinatura suspensa com sucesso");

        return ResponseEntity.ok(response);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}