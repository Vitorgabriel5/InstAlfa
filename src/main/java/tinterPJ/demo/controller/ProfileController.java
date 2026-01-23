package tinterPJ.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tinterPJ.demo.dto.UserProfileDTO;
import tinterPJ.demo.model.User;
import tinterPJ.demo.model.UserProfile;
import tinterPJ.demo.repository.UserProfileRepository;
import tinterPJ.demo.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileRepository profileRepository;
    private final UserRepository userRepository;

    // Obter meu perfil
    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMeuPerfil(){
        User user = getAuthenticatedUser();

        UserProfile profile = profileRepository.findByUsuario(user)
                .orElseThrow(()-> new RuntimeException("Perfil nao encontrado"));

        return ResponseEntity.ok(UserProfileDTO.fromEntity(profile));
    }

    //Criar/Atualizar perfil
    @PostMapping
    public ResponseEntity<UserProfileDTO> criarOuAtualizarPerfil(@RequestBody UserProfileDTO dto){
        User user = getAuthenticatedUser();

        UserProfile profile = profileRepository.findByUsuario(user)
                .orElse(new UserProfile());

        if (profile.getId() == null){
            profile.setUsuario(user);
        }

        profile.setBio(dto.getBio());
        profile.setDataNascimento(dto.getDataNascimento());
        profile.setGenero(dto.getGenero());
        profile.setInteresseEm(dto.getInteresseEm());
        profile.setCidade(dto.getCidade());
        profile.setEstado(dto.getEstado());
        profile.setPais(dto.getPais());
        profile.setRaioBusca(dto.getRaioBusca());
        profile.setIdadeMinima(dto.getIdadeMinima());
        profile.setIdadeMaxima(dto.getIdadeMaxima());
        profile.setFoto(dto.getFotos());
        profile.setInteresses(dto.getInteresses());
        profile.setProfissao(dto.getProfissao());
        profile.setEmpresa(dto.getEmpresa());
        profile.setEscola(dto.getEscola());

        // Atualizar localizacao se fornecida
        if (dto.getLatitude() != null && dto.getLongitude() != null){
            profile.setLatitude(dto.getLatitude());
            profile.setLongitude(dto.getLongitude());
            profile.setUltimaAtualizacao(LocalDateTime.now());
        }

        profile = profileRepository.save(profile);

        return ResponseEntity.ok(UserProfileDTO.fromEntity(profile));
    }

    // Atualizar localizacao
    @PutMapping("/localizacao")
    public ResponseEntity<Map<String, String>> autualizarLocalizacao(
            @RequestParam Double latitude,
            @RequestParam Double longitude){
    User user = getAuthenticatedUser();

    UserProfile profile = profileRepository.findByUsuario(user)
            .orElseThrow(()-> new RuntimeException("Perfil nao encontrado"));

    profile.setLatitude(latitude);
    profile.setLongitude(longitude);
    profile.setUltimaAtualizacao(LocalDateTime.now());

    profileRepository.save(profile);

    Map<String, String> response = new HashMap<>();
    response.put("status", "true");
    response.put("mensage", "Localizacao atualizada");

    return ResponseEntity.ok(response);
    }

    //Ativar/Desativar perfil
    @PutMapping("/toggle-ativo")
    public ResponseEntity<Map<String, Object>> togglePerfilAtivo(){
        User user = getAuthenticatedUser();

        UserProfile profile = profileRepository.findByUsuario(user)
                .orElseThrow(()-> new RuntimeException("Perfil nao encontrado"));

        profile.setPerfilAtivo(!profile.getPerfilAtivo());
        profileRepository.save(profile);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("mensage", profile.getPerfilAtivo());

        return ResponseEntity.ok(response);
    }

    // Ativar/Desativar visibilidade na busca
    @PutMapping("/toggle-visibilidade")
    public ResponseEntity<Map<String, Object>> toggleVisibilidade(){
        User user = getAuthenticatedUser();

        UserProfile profile = profileRepository.findByUsuario(user)
                .orElseThrow(()-> new RuntimeException("Perfil nao encontrado"));

        profile.setVisivelNaBusca(!profile.getVisivelNaBusca());
        profile = profileRepository.save(profile);

        Map<String, Object> response = new HashMap<>();
        response.put("status", true);
        response.put("mensage", profile.getVisivelNaBusca());

        return ResponseEntity.ok(response);
    }


    // Ver perfil do outro usuario (somente se houver match)
    @GetMapping("/{usuarioId}")
    public ResponseEntity<UserProfileDTO> verPerfilUsuario(@PathVariable Long usuarioId){
        User targetUser = userRepository.findById(usuarioId)
                .orElseThrow(()-> new RuntimeException("Perfil nao encontrado"));

        UserProfile profile = profileRepository.findByUsuario(targetUser)
                .orElseThrow(()-> new RuntimeException("Perfil nao encontrado"));

        return ResponseEntity.ok(UserProfileDTO.fromEntity(profile));
    }


    private User getAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
