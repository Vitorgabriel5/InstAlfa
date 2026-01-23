package tinterPJ.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tinterPJ.demo.dto.MatchDTO;
import tinterPJ.demo.dto.SwipeRequest;
import tinterPJ.demo.dto.UserCardDTO;
import tinterPJ.demo.model.User;
import tinterPJ.demo.service.MatchService;

import java.util.*;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // Busca perfis para dar swipe
    @GetMapping("/descobrir")
    public ResponseEntity<List<UserCardDTO>> descobrirPerfis(
            @RequestParam(defaultValue = "20") Integer limite){

        User user = getAuthenticateUser();
        List<UserCardDTO> perfis = matchService.buscarPerfilProximos(user.getId(), limite);

        return ResponseEntity.ok(perfis);
    }

    // Dar swipe (like/dislike/super like)
    @PostMapping("/swipe")
    public ResponseEntity<Map<String, Object>> darSwipe(@Valid @RequestBody SwipeRequest request) {
        User user = getAuthenticateUser();

        Optional<MatchDTO> match = matchService.darSwipe(
                user.getId(),
                request.getUsuarioDestinoId(),
                request.getTipo()
        );

        Map<String, Object> response = new HashMap<>();

        if (match.isPresent()) {
            response.put("match", true);
            response.put("message", "E um Match!");
            response.put("dados", match.get());
        } else{
            response.put("match", false);
            response.put("message", "Swipe registrado");
        }

        return ResponseEntity.ok(response);
    }

    // Listar meus matches
    @GetMapping("/meus-matches")
    public ResponseEntity<List<MatchDTO>> meusMatches(){
        User user = getAuthenticateUser();
        List<MatchDTO> matches = matchService.listMeusMatches(user.getId());

        return ResponseEntity.ok(matches);
    }

    //Desfazer match
    @DeleteMapping("/{matchId}")
    public ResponseEntity<Map<String, String>> desfazerMatch(@PathVariable Long matchId){
        User user = getAuthenticateUser();
        matchService.desfazerMatch(user.getId(), matchId);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Match desfeito");

        return ResponseEntity.ok(response);
    }

    // Bloquear usuario
    @PostMapping("/{matchId}/bloquear")
    public ResponseEntity<Map<String, String>> bloquearUsuario(@PathVariable Long matchId){
        User user = getAuthenticateUser();
        matchService.bloquearUsuario(user.getId(), matchId);

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Match bloqueado");

        return ResponseEntity.ok(response);
    }
    private User getAuthenticateUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
