package tinterPJ.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tinterPJ.demo.dto.ChatMessageDTO;
import tinterPJ.demo.dto.ConversationDTO;
import tinterPJ.demo.dto.SendMessageRequest;
import tinterPJ.demo.model.User;
import tinterPJ.demo.service.ChatService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Enviar mensagem (HTTP)
    @PostMapping("/enviar")
    public ResponseEntity<ChatMessageDTO> enviarMensagem(@Valid @RequestBody SendMessageRequest request) {
        User user = getAutheticatedUser();

        ChatMessageDTO message = chatService.enviarMensagem(
                user.getId(),
                request.getMatchId(),
                request.getConteudo(),
                request.getTipo()
        );
        return ResponseEntity.ok(message);
    }

    // Buscar mensagens de um match (paginado)
    @GetMapping("/mensagens/{matchId}")
    public ResponseEntity<Page<ChatMessageDTO>> buscarMensagens(
            @PathVariable Long matchId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {

        User user = getAutheticatedUser();
        Page<ChatMessageDTO> messages = chatService.buscarMensagens(
                user.getId(),
                matchId
                ,page
                ,size);

        return ResponseEntity.ok(messages);
    }

    //Listar todas as conversas
    @GetMapping("/conversas")
    public ResponseEntity<List<ConversationDTO>> listaConversas() {
        User user = getAutheticatedUser();
        List<ConversationDTO> conversations = chatService.listarConversas(user.getId());

        return ResponseEntity.ok(conversations);
    }

    // Marcar mensagem como lida
    @PutMapping("/mensagem/{mensagemId}/ler")
    public ResponseEntity<Map<String, String>> marcarComoLida(@PathVariable Long mensagemId) {
        User user = getAutheticatedUser();
        chatService.marcarComoLida(mensagemId, user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("mensage", "Mensagem marcada como lida");

        return ResponseEntity.ok(response);
    }

    // Marcar todas as mensagens de um match como lidas
    @PutMapping("/match/{matchId}/ler-todas")
    public ResponseEntity<Map<String, String>> marcarTodasComoLidas(@PathVariable Long matchId) {
        User user = getAutheticatedUser();
        chatService.marcarTodasComoLidas(matchId, user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Todas as mensagens marcadas como lidas");

        return ResponseEntity.ok(response);
    }

    //Contar mensagens nao lidas
    @GetMapping("/nao-lidas")
    public ResponseEntity<Map<String, Long>> contarNaoLidas(){
        User user = getAutheticatedUser();
        Long count = chatService.contarMensagensNaoLidas(user.getId());

        Map<String,Long> response = new HashMap<>();
        response.put("total",count);

        return ResponseEntity.ok(response);
    }

    //Deletar mensagem
    @DeleteMapping("/mensagem/{mensagemId}")
    public ResponseEntity<Map<String, String>> deletarMensagem(@PathVariable Long mensagemId) {
        User user = getAutheticatedUser();
        chatService.deletarMensagem(mensagemId, user.getId());

        Map<String, String> response = new HashMap<>();
        response.put("success", "true");
        response.put("message", "Mensagem deletada");

        return ResponseEntity.ok(response);
    }

    private User getAutheticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}

@Controller
@RequiredArgsConstructor
class WebSocketController {

    private final ChatService chatService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        chatService.enviarMensagem(
                user.getId()
                ,request.getMatchId()
                ,request.getConteudo()
                ,request.getTipo()
        );
    }
}
