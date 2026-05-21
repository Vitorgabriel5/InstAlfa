package AlfaInsta.demo.controller;

import AlfaInsta.demo.dto.OAuthLoginRequest;
import AlfaInsta.demo.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/oauth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✅ Permitir CORS para desenvolvimento
public class OAuthController {

    private final OAuthService oAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody OAuthLoginRequest request) {
        try {
            String token = oAuthService.authenticateWithGoogle(request.getIdToken());


            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login com Google realizado com sucesso");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // ✅ Log do erro para debug
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("type", e.getClass().getSimpleName());

            return ResponseEntity.badRequest().body(error);
        }
    }


    @GetMapping("/status")
    public ResponseEntity<?> checkStatus() {
        return ResponseEntity.ok(Map.of(
                "google", "enabled",
                "apple", "disabled"
        ));
    }
}