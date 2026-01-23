package tinterPJ.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tinterPJ.demo.dto.MatchDTO;
import tinterPJ.demo.dto.UserCardDTO;
import tinterPJ.demo.messaging.events.MatchNotificationEvent;
import tinterPJ.demo.messaging.producer.MatchNotificationProducer;
import tinterPJ.demo.model.*;
import tinterPJ.demo.repository.MatchRepository;
import tinterPJ.demo.repository.SwipeRepository;
import tinterPJ.demo.repository.UserProfileRepository;
import tinterPJ.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final UserProfileRepository profileRepository;
    private final SwipeRepository swipeRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final MatchNotificationProducer matchNotificationProducer;


    //Buscar perfis proximos para o usuario dar swipe
    public List<UserCardDTO> buscarPerfilProximos(Long usuarioId,Integer limite){
        User usuario = userRepository.findById(usuarioId)
        .orElseThrow(()->new RuntimeException("Usuario nao encontrado"));

        UserProfile meuPerfil = profileRepository.findByUsuario(usuario)
                .orElseThrow(()->new RuntimeException("Perfil nao encontrado"));

        if (!meuPerfil.getPerfilAtivo() || !meuPerfil.getVisivelNaBusca()){
            throw new RuntimeException("Perfil nao esta ativo para buscar outros usuarios");
        }

        List<UserProfile> perfis = profileRepository.findPerfilsProximos(
                usuarioId,
                meuPerfil.getLatitude(),
                meuPerfil.getLongitude(),
                meuPerfil.getRaioBusca(),
                limite != null ? limite : 20
        );

        return perfis.stream()
                .filter(p -> filtrarPorPreferencias(meuPerfil, p))
                .map(this::converterParaCard)
                .collect(Collectors.toList());
    }

    private boolean filtrarPorPreferencias(UserProfile meuPerfil, UserProfile outroPerfil){
        //Filtrar por genero de interesse
        if (meuPerfil.getInteresseEm() != Gender.TODOS
            && outroPerfil.getGenero() != meuPerfil.getInteresseEm()){
            return false;
        }

        //Filtrar por idade
        Integer idadeOutro = outroPerfil.getIdade();
        if (idadeOutro != null) {
            if (idadeOutro < meuPerfil.getIdadeMinima() || idadeOutro > meuPerfil.getIdadeMaxima()){
                return false;
            }
        }
        return true;
    }

    //Dar swipe (like, dislike, super like)
    public Optional<MatchDTO> darSwipe(Long usuarioOrigemId, Long usuarioDestinoId, SwipeType tipo){
        User origem = userRepository.findById(usuarioOrigemId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        User destino = userRepository.findById(usuarioDestinoId)
                .orElseThrow(() -> new RuntimeException("Usuario destinado nao encontrado"));

        // verificar se ja existe swipe
        if (swipeRepository.existsByUsuarioOrigemAndUsuarioDestino(origem, destino)){
            throw new RuntimeException("Voce ja deu swipe neste usuario");
        }

        // criar o swipe
        Swipe swipe = new Swipe();
        swipe.setUsuarioOrigem(origem);
        swipe.setUsuarioDestino(destino);
        swipe.setTipo(tipo);
        swipeRepository.save(swipe);

        // Verificar se houve match (se o outro usuario tambem deu like)
        if (tipo == SwipeType.LIKE || tipo == SwipeType.SUPER_LIKE){
            Optional<Swipe> swipeReverso = swipeRepository
                    .findByUsuarioOrigemAndUsuarioDestino(destino, origem);



            if (swipeReverso.isPresent() &&
                    (swipeReverso.get().getTipo() == SwipeType.LIKE ||
                            swipeReverso.get().getTipo() == SwipeType.SUPER_LIKE)) {


                // MATCH! Criar registro de match
                Match match = new Match();
                match.setUsuario1(origem);
                match.setUsuario2(destino);
                match.setAtivo(true);
                match = matchRepository.save(match);

                MatchNotificationEvent event = MatchNotificationEvent.builder()
                        .matchId(match.getId())
                        .user1Id(origem.getId())
                        .user1Nome(origem.getNome())
                        .user2Id(destino.getId())
                        .user2Nome(destino.getNome())
                        .dataMatch(LocalDateTime.now())
                        .build();

                matchNotificationProducer.sendMacthNotification(event);

                return Optional.of(MatchDTO.fromEntity(match,origem));
            }
        }
        return Optional.empty();
    }

    // Listar meus matches
    public List<MatchDTO> listMeusMatches(Long usuarioId){
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario nao encontrado"));

        List<Match> matches = matchRepository.findMatchesByUsuario(usuario);

        return matches.stream()
                .map(m -> MatchDTO.fromEntity(m,usuario))
                .collect(Collectors.toList());
    }

    // Desfazer match
    @Transactional
    public void desfazerMatch (Long usuarioId, Long matchId){
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match nao encontrado"));
        if (!match.contemUsuario(usuarioId)) {
            throw new RuntimeException("Voce nao faz parte deste match");
        }

        match.setAtivo(false);
        match.setDataDesfez(java.time.LocalDateTime.now());
        matchRepository.save(match);
    }

    // Bloquear usuario
    @Transactional
    public void bloquearUsuario(Long usuarioId, Long matchId){
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match nao encontrado"));

        if (!match.contemUsuario(usuarioId)) {
            throw new RuntimeException("Voce nao faz parte deste match");
        }
        match.setAtivo(false);
        match.setBloqueado(true);
        match.setBloqueadoPorId(usuarioId);
        matchRepository.save(match);
    }

    //Calcular distancia entre dois pontos (formula de Haversine)
    public double calcularDistancia(Double lat1, Double long1, Double lat2, Double long2){
        final int R = 6371;

        double latDistance = Math.toRadians(lat2 - lat1);
        double longDistance = Math.toRadians(long2 - long1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(longDistance / 2) * Math.sin(longDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private UserCardDTO converterParaCard(UserProfile profile){
        return  UserCardDTO.builder()
                .usuarioId(profile.getUsuario().getId())
                .nome(profile.getUsuario().getNome())
                .idade(profile.getIdade())
                .bio(profile.getBio())
                .cidade(profile.getCidade())
                .fotos(profile.getFoto())
                .interesses(profile.getInteresses())
                .profissao(profile.getProfissao())
                .distanciaKm(null)
                .verificado(profile.getVerificado())
                .build();
    }
}
