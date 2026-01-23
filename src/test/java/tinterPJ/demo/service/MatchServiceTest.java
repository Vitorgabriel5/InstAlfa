package tinterPJ.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tinterPJ.demo.dto.MatchDTO;
import tinterPJ.demo.messaging.events.MatchNotificationEvent;
import tinterPJ.demo.messaging.producer.MatchNotificationProducer;
import tinterPJ.demo.model.Match;
import tinterPJ.demo.model.Swipe;
import tinterPJ.demo.model.SwipeType;
import tinterPJ.demo.model.User;
import tinterPJ.demo.repository.MatchRepository;
import tinterPJ.demo.repository.SwipeRepository;
import tinterPJ.demo.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SwipeRepository swipeRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchNotificationProducer matchNotificationProducer;

    @InjectMocks
    private MatchService matchService;

    private User user1;

    private User user2;

    private Match match;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setId(1L);
        user1.setNome("Joao");
        user1.setUsername("joao");

        user2 = new User();
        user2.setId(2L);
        user2.setNome("Maria");
        user2.setUsername("maria");


        match = new Match();
        match.setId(1L);
        match.setUsuario1(user1);
        match.setUsuario2(user2);
        match.setAtivo(true);
    }

    @Test
    void testDarSwipe_Like_semMatch(){

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(swipeRepository.existsByUsuarioOrigemAndUsuarioDestino(user1,user2)).thenReturn(false);
        when(swipeRepository.findByUsuarioOrigemAndUsuarioDestino(user2,user1)).thenReturn(Optional.empty());

        Optional<MatchDTO> result = matchService.darSwipe(1L,2L, SwipeType.LIKE);

        assertFalse(result.isPresent());
        verify(swipeRepository, times(1)).save(any(Swipe.class));
        verify(matchRepository, never()).save(any());
        verify(matchNotificationProducer, never()).sendMacthNotification(any());
    }

    @Test
    void testDarSwipe_Like_ComMatch(){

        user1.setId(1L);
        user2.setId(2L);
        user1.setNome("Joao");
        user2.setNome("Maria");

        Swipe swipeReverso = new Swipe();
        swipeReverso.setUsuarioOrigem(user2);
        swipeReverso.setUsuarioDestino(user1);
        swipeReverso.setTipo(SwipeType.LIKE);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(swipeRepository.existsByUsuarioOrigemAndUsuarioDestino(user1,user2)).thenReturn(false);
        when(swipeRepository.findByUsuarioOrigemAndUsuarioDestino(user2,user1)).thenReturn(Optional.of(swipeReverso));

        when(swipeRepository.save(any(Swipe.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> {
            Match m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        Optional<MatchDTO> result = matchService.darSwipe(1L,2L, SwipeType.LIKE);

        assertTrue(result.isPresent());
        assertEquals(2L, result.get().getUsuarioId());
        assertEquals("Maria", result.get().getUsuarioNome());

        verify(swipeRepository, times(1)).save(any(Swipe.class));
        verify(matchRepository, times(1)).save(any(Match.class));

        ArgumentCaptor<MatchNotificationEvent> eventCaptor = ArgumentCaptor.forClass(MatchNotificationEvent.class);
        verify(matchNotificationProducer, times(1)).sendMacthNotification(eventCaptor.capture());

        MatchNotificationEvent event = eventCaptor.getValue();
        assertNotNull(event);
        assertEquals(1L, event.getMatchId());
        assertEquals(1L, event.getUser1Id());
        assertEquals("Joao", event.getUser1Nome());
        assertEquals(2L, event.getUser2Id());
        assertEquals("Maria", event.getUser2Nome());
    }

    @Test
    void testDarSwipe_Dislike_NaoCriaMatch(){

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(swipeRepository.existsByUsuarioOrigemAndUsuarioDestino(user1,user2)).thenReturn(false);

        Optional<MatchDTO> result = matchService.darSwipe(1L,2L, SwipeType.DISLIKE);

        assertFalse(result.isPresent());
        verify(swipeRepository, times(1)).save(any(Swipe.class));
        verify(matchRepository, never()).save(any());
    }

    @Test
    void testDarSwipe_SwipeJaExiste_ThrownsException(){

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(swipeRepository.existsByUsuarioOrigemAndUsuarioDestino(user1,user2)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> {
            matchService.darSwipe(1L,2L, SwipeType.LIKE);
        });
    }
}