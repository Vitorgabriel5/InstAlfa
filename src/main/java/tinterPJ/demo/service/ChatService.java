package tinterPJ.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.ChatMessageDTO;
import tinterPJ.demo.dto.ConversationDTO;
import tinterPJ.demo.messaging.events.ChatMessageEvent;
import tinterPJ.demo.messaging.producer.ChatMessageProducer;
import tinterPJ.demo.model.ChatMessage;
import tinterPJ.demo.model.Match;
import tinterPJ.demo.model.MessageType;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.ChatMessageRepository;
import tinterPJ.demo.repository.MatchRepository;
import tinterPJ.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository messageRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageProducer chatMessageProducer;


    // Enviar messagem
    @Transactional
    public ChatMessageDTO enviarMensagem(Long remetenteId, Long matchId, String conteudo, MessageType tipo){
        User remetente = userRepository.findById(remetenteId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match nao encontrado"));

        if (!match.getAtivo()){
            throw new RuntimeException("Match nao esta ativo");
        }

        if (!match.contemUsuario(remetenteId)){
            throw new RuntimeException("Voce nao faz parte deste match");
        }

        User destinatario = match.getOutroUsuario(remetenteId);

        ChatMessage message = new ChatMessage();
        message.setMatch(match);
        message.setRemetente(remetente);
        message.setDestinatario(destinatario);
        message.setConteudo(conteudo);
        message.setTipoMensagem(tipo);
        message.setLida(false);

        message = messageRepository.save(message);

        ChatMessageEvent event = ChatMessageEvent.builder()
                .messageId(message.getId())
                .matchId(matchId)
                .remetenteId(remetenteId)
                .remetenteNome(remetente.getNome())
                .destinatarioId(destinatario.getId())
                .conteudo(conteudo)
                .tipoMensagem(tipo.name())
                .dataEnvio(message.getDataEnvio())
                .build();

        chatMessageProducer.sendMessage(event);

        return ChatMessageDTO.fromEntity(message);
    }

    // Buscar mensagens de um match (paginado)
    public Page<ChatMessageDTO> buscarMensagens(Long usuarioId, Long matchId, int page, int size){
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match nao encontrado"));

        if (!match.contemUsuario(usuarioId)){
            throw new RuntimeException("Voce nao faz parte deste match");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = messageRepository.findByMatchOrderByDataEnvioDesc(match,pageable);

        return messages.map(ChatMessageDTO::fromEntity);
    }

    // Listar todas as conversas do usuario
    public List<ConversationDTO> listarConversas(Long usuarioId){
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        List<Match> matches = matchRepository.findMatchesByUsuario(usuario);

        return matches.stream()
                .map(match -> {
                    User outroUsuario = match.getOutroUsuario(usuarioId);
                    Long naoLida = messageRepository.countUnreadMessgesByMatch(match, usuario);

                    // Buscar ultima mensagem
                    Page<ChatMessage> lastMessage = messageRepository.findByMatchOrderByDataEnvioDesc(
                            match,
                            PageRequest.of(0,1)
                    );

                    ChatMessageDTO ultimaMensagem = lastMessage.hasContent()
                            ? ChatMessageDTO.fromEntity(lastMessage.getContent().get(0))
                            : null;

                    return ConversationDTO.builder()
                            .matchId(match.getId())
                            .usuarioId(outroUsuario.getId())
                            .usuarioNome(outroUsuario.getNome())
                            .ultimaMensagem(ultimaMensagem)
                            .mensagemNaoLida(naoLida)
                            .dataMatch(match.getDataMatch())
                            .build();
                })
                .sorted((c1, c2) ->{
                    LocalDateTime d1 = c1.getUltimaMensagem() != null
                            ? c1.getUltimaMensagem().getDataEnvio()
                            : c1.getDataMatch();
                    LocalDateTime d2 = c2.getUltimaMensagem() != null
                            ? c2.getUltimaMensagem().getDataEnvio()
                            : c2.getDataMatch();
                    return d2.compareTo(d1);
                })
                .collect(Collectors.toList());
    }

    // Marca messagem como lida
    @Transactional
    public void marcarComoLida(Long mensgemId, Long usuarioId) {
        ChatMessage message = messageRepository.findById(mensgemId)
                .orElseThrow(() -> new RuntimeException("Mensagem nao encontrada"));

        if (!message.getDestinatario().getId().equals(usuarioId)){
            throw new RuntimeException("Voce nao e o destinatario desta mensagem");
        }

        if (!message.getLida()){
            message.setLida(true);
            message.setDataEnvio(LocalDateTime.now());
            messageRepository.save(message);

            // Notificar remetente que a mensagem foi lida
            messagingTemplate.convertAndSendToUser(
                    message.getRemetente().getId().toString(),
                    "/queue/read-receipts",
                    mensgemId
            );
        }
    }

    // Marcar todas as mensagens de um match como lidas
    @Transactional
    public void marcarTodasComoLidas(Long matchId, Long usuarioId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match nao encontrado"));

        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        List<ChatMessage> messages = messageRepository.findByMatchOrderByDataEnvioAsc(match);

        messages.stream()
                .filter(m -> m.getDestinatario().getId().equals(usuarioId) && !m.getLida())
                .forEach(m -> {
                    m.setLida(true);
                    m.setDataLeitura(LocalDateTime.now());
                });
        messageRepository.saveAll(messages);
    }

    //Contar mensagens nao lidas
    public Long contarMensagensNaoLidas(Long usuarioId){
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        return messageRepository.countUnreadMessages(usuario);
    }

    // Deletar mensagem
    @Transactional
    public void deletarMensagem(Long mensgemId, Long usuarioId) {
        ChatMessage message = messageRepository.findById(mensgemId)
                .orElseThrow(() -> new RuntimeException("Mensagem nao encontrada"));

        if (!message.getRemetente().getId().equals(usuarioId)){
            throw new RuntimeException("Voce nao pode deletar mensagens de outros usuarios");
        }

        message.setDeletada(true);
        messageRepository.save(message);
    }
}
