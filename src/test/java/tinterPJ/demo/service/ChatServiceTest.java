package tinterPJ.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tinterPJ.demo.dto.ChatMessageDTO;
import tinterPJ.demo.messaging.producer.ChatMessageProducer;
import tinterPJ.demo.model.ChatMessage;
import tinterPJ.demo.model.Match;
import tinterPJ.demo.model.MessageType;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.ChatMessageRepository;
import tinterPJ.demo.repository.MatchRepository;
import tinterPJ.demo.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ChatMessageProducer chatMessageProducer;

    @InjectMocks
    private ChatService chatService;

    private User user1;
    private User user2;
    private Match match;

    @BeforeEach
    void setUp() {

        user1 = new User();
        user1.setId(1L);
        user1.setNome("Joao");

        user2 = new User();
        user2.setId(2L);
        user2.setNome("Maria");

        match = new Match();
        match.setId(1L);
        match.setUsuario1(user1);
        match.setUsuario2(user2);
        match.setAtivo(true);
    }

    @Test
    void testEnviarMensagem_Sucesso() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(messageRepository.save(any())).thenAnswer(i -> {
            ChatMessage msg = (ChatMessage) i.getArguments()[0];
            msg.setId(1L);
            return msg;
        });

        ChatMessageDTO result = chatService.enviarMensagem(1L, 1L, "Ola!", MessageType.TEXT);

        assertNotNull(result);
        assertEquals("Ola!", result.getConteudo());
        verify(messageRepository, times(1)).save(any());
        verify(chatMessageProducer, times(1)).sendMessage(any());
    }

    @Test
    void testEnviarMensagem_MatchInativo_ThrowsException() {
        match.setAtivo(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        assertThrows(RuntimeException.class, () -> {
           chatService.enviarMensagem(1L, 1L, "Ola!", MessageType.TEXT);
        });
    }

    @Test
    void testEnviarMensagem_UsuarioNaoEhDoMatch_ThrowsException() {

        User user3 = new User();
        user3.setId(3L);

        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));
        when(matchRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> {
           chatService.enviarMensagem(3L, 1L, "Ola!", MessageType.TEXT);
        });
    }
}